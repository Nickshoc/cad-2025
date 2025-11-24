package com.example.demo.Service;

import com.example.demo.OrderHistory;
import com.example.demo.RepairOrder;
import com.example.demo.Repository.OrderHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderHistoryService {

    private final OrderHistoryRepository orderHistoryRepository;

    @Autowired
    public OrderHistoryService(OrderHistoryRepository orderHistoryRepository) {
        this.orderHistoryRepository = orderHistoryRepository;
    }

    public OrderHistory createStatusChangeHistory(RepairOrder order, String oldStatus, String newStatus, String changedBy) {
        OrderHistory history = new OrderHistory(oldStatus, newStatus, changedBy, order);
        return orderHistoryRepository.save(history);
    }

    public OrderHistory createFieldChangeHistory(RepairOrder order, String fieldChanged,
                                                 String oldValue, String newValue, String changedBy) {
        OrderHistory history = new OrderHistory(fieldChanged, oldValue, newValue, changedBy, order);
        return orderHistoryRepository.save(history);
    }

    public List<OrderHistory> getOrderHistory(Long orderId) {
        return orderHistoryRepository.findByRepairOrderIdOrderByChangeDateDesc(orderId);
    }

    public List<OrderHistory> getUserOrderHistory(String changedBy) {
        return orderHistoryRepository.findByChangedByOrderByChangeDateDesc(changedBy);
    }

    public void recordOrderCreation(RepairOrder order) {
        OrderHistory history = new OrderHistory(
                "creation",
                null,
                "CREATED",
                order.getCreatedBy(),
                order
        );
        history.setChangeDescription("Заявка создана");
        orderHistoryRepository.save(history);
    }
}