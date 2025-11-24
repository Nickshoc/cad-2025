package com.example.demo.Service;

import com.example.demo.RepairOrder;
import com.example.demo.Repository.RepairOrderRepository;
import com.example.demo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RepairOrderService {

    private final RepairOrderRepository orderRepository;
    private final OrderHistoryService orderHistoryService;

    @Autowired
    public RepairOrderService(RepairOrderRepository orderRepository,
                              OrderHistoryService orderHistoryService) {
        this.orderRepository = orderRepository;
        this.orderHistoryService = orderHistoryService;
    }

    public RepairOrder addOrder(RepairOrder order) {
        RepairOrder savedOrder = orderRepository.save(order);
        // Записываем создание заявки в историю
        orderHistoryService.recordOrderCreation(savedOrder);
        return savedOrder;
    }

    public List<RepairOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    // Новый метод для получения заказов по пользователю
    public List<RepairOrder> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }

    // Старый метод для обратной совместимости
    public List<RepairOrder> getOrdersByUser(String username) {
        return orderRepository.findByCreatedBy(username);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    public Optional<RepairOrder> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public RepairOrder updateOrder(RepairOrder order) {
        return orderRepository.save(order);
    }

    public List<RepairOrder> getOverdueOrders() {
        return orderRepository.findOverdueOrders();
    }

    public List<RepairOrder> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    public RepairOrder changeOrderStatus(Long orderId, String newStatus, String changedBy) {
        Optional<RepairOrder> orderOpt = getOrderById(orderId);
        if (orderOpt.isPresent()) {
            RepairOrder order = orderOpt.get();
            String oldStatus = order.getStatus();
            order.setStatus(newStatus);
            RepairOrder updatedOrder = updateOrder(order);

            // Записываем изменение статуса в историю
            orderHistoryService.createStatusChangeHistory(order, oldStatus, newStatus, changedBy);

            return updatedOrder;
        }
        return null;
    }

    public OrderHistoryService getOrderHistoryService() {
        return orderHistoryService;
    }
}