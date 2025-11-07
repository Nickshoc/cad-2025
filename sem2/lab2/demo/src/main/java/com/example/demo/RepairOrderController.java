package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
        List<RepairOrder> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        model.addAttribute("today", LocalDate.now());
        return "index";
    }

    @PostMapping("/addOrder")
    public String addOrder(@ModelAttribute RepairOrder order,
                           @RequestParam String deadline) {
        order.setDeadline(LocalDate.parse(deadline));
        orderService.addOrder(order);
        return "redirect:/";
    }

    @GetMapping("/deleteOrder/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return "redirect:/";
    }

    @PostMapping("/updateOrder/{id}")
    public String updateOrder(@PathVariable Long id, @RequestParam boolean completed) {
        orderService.getOrderById(id).ifPresent(order -> {
            order.setCompleted(completed);
            orderService.updateOrder(order);
        });
        return "redirect:/";
    }

    @PostMapping("/updateOrderDetails")
    public String updateOrderDetails(@ModelAttribute RepairOrder updatedOrder,
                                     @RequestParam String deadline) {
        updatedOrder.setDeadline(LocalDate.parse(deadline));
        orderService.updateOrder(updatedOrder);
        return "redirect:/";
    }
}
