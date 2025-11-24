package com.example.demo.Repository;

import com.example.demo.OrderHistory;
import com.example.demo.RepairOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    List<OrderHistory> findByRepairOrderOrderByChangeDateDesc(RepairOrder repairOrder);
    List<OrderHistory> findByRepairOrderIdOrderByChangeDateDesc(Long orderId);
    List<OrderHistory> findByChangedByOrderByChangeDateDesc(String changedBy);
}