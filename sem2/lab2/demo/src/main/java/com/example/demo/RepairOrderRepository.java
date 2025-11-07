package com.example.demo;

import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class RepairOrderRepository {
    private List<RepairOrder> orderList = new ArrayList<>();

    public void addOrder(RepairOrder order) {
        orderList.add(order);
    }

    public List<RepairOrder> getAllOrders() {
        return orderList;
    }

    public void deleteOrder(Long id) {
        orderList.removeIf(order -> order.getId().equals(id));
    }

    public Optional<RepairOrder> findById(Long id) {
        return orderList.stream()
                .filter(order -> order.getId().equals(id))
                .findFirst();
    }

    public void updateOrder(RepairOrder updatedOrder) {
        for (int i = 0; i < orderList.size(); i++) {
            if (orderList.get(i).getId().equals(updatedOrder.getId())) {
                orderList.set(i, updatedOrder);
                break;
            }
        }
    }
}
