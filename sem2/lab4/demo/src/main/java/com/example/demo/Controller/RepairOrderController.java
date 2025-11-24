package com.example.demo.Controller;

import com.example.demo.RepairOrder;
import com.example.demo.Service.RepairOrderService;
import com.example.demo.User;
import com.example.demo.Service.UserService;
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
    private final UserService userService;

    public RepairOrderController(RepairOrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
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
            // Операторы и администраторы видят все заказы
            orders = orderService.getAllOrders();
        } else {
            // Обычные пользователи видят только свои заказы
            User currentUser = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            orders = orderService.getOrdersByUser(currentUser);
        }

        // Получаем просроченные заказы для операторов и администраторов
        if (isOperator || isAdmin) {
            List<RepairOrder> overdueOrders = orderService.getOverdueOrders();
            model.addAttribute("overdueCount", overdueOrders.size());
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

        User currentUser = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        order.setDeadline(LocalDate.parse(deadline));
        order.setUser(currentUser); // Привязываем заказ к пользователю
        orderService.addOrder(order);
        return "redirect:/";
    }

    // Остальные методы остаются без изменений...
    @PreAuthorize("hasRole('OPERATOR') or hasRole('ADMIN')")
    @PostMapping("/acceptOrder/{id}")
    public String acceptOrder(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        orderService.changeOrderStatus(id, "ACCEPTED", username);
        return "redirect:/";
    }

    @PreAuthorize("hasRole('OPERATOR') or hasRole('ADMIN')")
    @PostMapping("/rejectOrder/{id}")
    public String rejectOrder(@PathVariable Long id, @RequestParam String comment) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        orderService.getOrderById(id).ifPresent(order -> {
            order.setOperatorComment(comment);
            orderService.updateOrder(order);
            orderService.changeOrderStatus(id, "REJECTED", username);
        });
        return "redirect:/";
    }

    @PreAuthorize("hasRole('OPERATOR') or hasRole('ADMIN')")
    @PostMapping("/completeOrder/{id}")
    public String completeOrder(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        orderService.changeOrderStatus(id, "COMPLETED", username);
        return "redirect:/";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/deleteOrder/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return "redirect:/";
    }

    @PreAuthorize("hasRole('OPERATOR') or hasRole('ADMIN')")
    @GetMapping("/overdue")
    public String showOverdueOrders(Model model) {
        List<RepairOrder> overdueOrders = orderService.getOverdueOrders();
        model.addAttribute("orders", overdueOrders);
        model.addAttribute("today", LocalDate.now());
        return "overdue";
    }
}