package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.util.List;

@Controller
public class RepairOrderController {
    private final RepairOrderService orderService;

    public RepairOrderController(RepairOrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/")
    public String index(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }

        String username = auth.getName();
        boolean isOperator = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_OPERATOR"));
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        boolean isUser = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"));

        List<RepairOrder> orders;

        if (isOperator || isAdmin) {
            orders = orderService.getAllOrders();
        } else {
            orders = orderService.getOrdersByUser(username);
        }

        model.addAttribute("orders", orders);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("isOperator", isOperator);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isUser", isUser);
        model.addAttribute("username", username);
        return "index";
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/addOrder")
    public String addOrder(@ModelAttribute RepairOrder order,
                           @RequestParam String deadline) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        order.setDeadline(LocalDate.parse(deadline));
        order.setCreatedBy(username);
        orderService.addOrder(order);
        return "redirect:/";
    }

    @PreAuthorize("hasRole('OPERATOR') or hasRole('ADMIN')")
    @PostMapping("/acceptOrder/{id}")
    public String acceptOrder(@PathVariable Long id) {
        orderService.getOrderById(id).ifPresent(order -> {
            order.setStatus("ACCEPTED");
            orderService.updateOrder(order);
        });
        return "redirect:/";
    }

    @PreAuthorize("hasRole('OPERATOR') or hasRole('ADMIN')")
    @PostMapping("/rejectOrder/{id}")
    public String rejectOrder(@PathVariable Long id, @RequestParam String comment) {
        orderService.getOrderById(id).ifPresent(order -> {
            order.setStatus("REJECTED");
            order.setOperatorComment(comment);
            orderService.updateOrder(order);
        });
        return "redirect:/";
    }

    @PreAuthorize("hasRole('OPERATOR') or hasRole('ADMIN')")
    @PostMapping("/completeOrder/{id}")
    public String completeOrder(@PathVariable Long id) {
        orderService.getOrderById(id).ifPresent(order -> {
            order.setStatus("COMPLETED");
            orderService.updateOrder(order);
        });
        return "redirect:/";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/deleteOrder/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return "redirect:/";
    }
}