package com.example.demo.Controller;

import com.example.demo.RepairOrder;
import com.example.demo.RepairOrderResponseDTO;
import com.example.demo.Service.RepairOrderService;
import com.example.demo.User;
import com.example.demo.Service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@Transactional
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
        User currentUser = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<RepairOrder> orders;

        boolean isOperator = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_OPERATOR"));
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        boolean isUser = !isOperator && !isAdmin; // Добавляем эту переменную

        if (isOperator || isAdmin) {
            orders = orderService.getAllOrders();
            System.out.println("Showing ALL orders for " + username + " (Operator/Admin). Count: " + orders.size());
        } else {
            orders = orderService.getOrdersByUser(currentUser);
            System.out.println("Showing USER orders for " + username + ". Count: " + orders.size());
        }

        int overdueCount = 0;
        if (isOperator || isAdmin) {
            List<RepairOrder> overdueOrders = orderService.getOverdueOrders();
            overdueCount = overdueOrders.size();
        }

        model.addAttribute("orders", orders);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("isOperator", isOperator);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isUser", isUser); // Добавляем эту строку
        model.addAttribute("overdueCount", overdueCount); // Добавляем счетчик просроченных
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
        order.setUser(currentUser);
        orderService.addOrder(order);
        return "redirect:/";
    }

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
    @GetMapping("/api/orders")
    @PreAuthorize("hasRole('USER') or hasRole('OPERATOR') or hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<List<RepairOrderResponseDTO>> getAllOrders(Authentication authentication) {
        String username = authentication.getName();
        System.out.println("=== API CALL: /api/orders by " + username + " ===");

        User currentUser = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<RepairOrder> orders;
        boolean isOperator = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_OPERATOR"));
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (isOperator || isAdmin) {
            orders = orderService.getAllOrders();
            System.out.println("API: Returning ALL " + orders.size() + " orders for " + username);
        } else {
            orders = orderService.getOrdersByUser(currentUser);
            System.out.println("API: Returning USER orders for " + username + ", count: " + orders.size());
        }

        List<RepairOrderResponseDTO> orderDTOs = orders.stream()
                .map(RepairOrderResponseDTO::new)
                .collect(Collectors.toList());

        for (RepairOrderResponseDTO dto : orderDTOs) {
            System.out.println("DTO: id=" + dto.getId() +
                    ", deviceType=" + dto.getDeviceType() +
                    ", clientName=" + dto.getClientName() +
                    ", createdByUsername=" + dto.getCreatedByUsername());
        }

        return ResponseEntity.ok(orderDTOs);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/api/orders/{id}")
    public ResponseEntity<?> deleteOrderApi(@PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка удаления заказа");
        }
    }
    @GetMapping("/api/user/role")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getUserRoleApi(Authentication authentication) {
        String username = authentication.getName();

        System.out.println("=== GET USER ROLE API CALLED ===");
        System.out.println("Username: " + username);

        User currentUser = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        System.out.println("User found: " + currentUser.getUsername());
        System.out.println("Role from DB: " + currentUser.getRole());

        Map<String, String> response = new HashMap<>();
        response.put("username", username);
        response.put("role", currentUser.getRole());

        return ResponseEntity.ok(response);
    }


}