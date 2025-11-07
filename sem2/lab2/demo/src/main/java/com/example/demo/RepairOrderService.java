package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RepairOrderService {
    private final RepairOrderRepository orderRepository;

    @Autowired
    public RepairOrderService(RepairOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void addOrder(RepairOrder order) {
        orderRepository.addOrder(order);
    }

    public List<RepairOrder> getAllOrders() {
        return orderRepository.getAllOrders();
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteOrder(id);
    }

    public Optional<RepairOrder> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public void updateOrder(RepairOrder order) {
        orderRepository.updateOrder(order);
    }
}