package com.example.demo.Repository;

import com.example.demo.RepairOrder;
import com.example.demo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RepairOrderRepository extends JpaRepository<RepairOrder, Long> {

    List<RepairOrder> findByCreatedBy(String createdBy);

    List<RepairOrder> findByUser(User user);

    @Query("SELECT ro FROM RepairOrder ro WHERE ro.deadline < CURRENT_DATE AND ro.status != 'COMPLETED'")
    List<RepairOrder> findOverdueOrders();

    List<RepairOrder> findByStatus(String status);
}
