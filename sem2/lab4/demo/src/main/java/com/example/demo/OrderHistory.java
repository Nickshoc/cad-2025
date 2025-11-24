package com.example.demo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_history")
public class OrderHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "change_date", nullable = false)
    private LocalDateTime changeDate;

    @Column(name = "old_status")
    private String oldStatus;

    @Column(name = "new_status")
    private String newStatus;

    @Column(name = "changed_by")
    private String changedBy;

    @Column(name = "field_changed")
    private String fieldChanged;

    @Column(name = "old_value")
    private String oldValue;

    @Column(name = "new_value")
    private String newValue;

    @Column(name = "change_description")
    private String changeDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private RepairOrder repairOrder;

    // Конструкторы
    public OrderHistory() {}

    public OrderHistory(LocalDateTime changeDate, String oldStatus, String newStatus,
                        String changedBy, RepairOrder repairOrder) {
        this.changeDate = changeDate;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.repairOrder = repairOrder;
        this.fieldChanged = "status";
        this.changeDescription = "Статус изменен с '" + oldStatus + "' на '" + newStatus + "'";
    }

    public OrderHistory(String oldStatus, String newStatus, String changedBy, RepairOrder repairOrder) {
        this(LocalDateTime.now(), oldStatus, newStatus, changedBy, repairOrder);
    }

    public OrderHistory(String fieldChanged, String oldValue, String newValue,
                        String changedBy, RepairOrder repairOrder) {
        this.changeDate = LocalDateTime.now();
        this.fieldChanged = fieldChanged;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changedBy = changedBy;
        this.repairOrder = repairOrder;
        this.changeDescription = "Поле '" + fieldChanged + "' изменено с '" + oldValue + "' на '" + newValue + "'";
    }

    public OrderHistory(String fieldChanged, String oldValue, String newValue,
                        String changedBy, String changeDescription, RepairOrder repairOrder) {
        this.changeDate = LocalDateTime.now();
        this.fieldChanged = fieldChanged;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changedBy = changedBy;
        this.changeDescription = changeDescription;
        this.repairOrder = repairOrder;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public String getFieldChanged() {
        return fieldChanged;
    }

    public void setFieldChanged(String fieldChanged) {
        this.fieldChanged = fieldChanged;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getChangeDescription() {
        return changeDescription;
    }

    public void setChangeDescription(String changeDescription) {
        this.changeDescription = changeDescription;
    }

    public RepairOrder getRepairOrder() {
        return repairOrder;
    }

    public void setRepairOrder(RepairOrder repairOrder) {
        this.repairOrder = repairOrder;
    }

    @Override
    public String toString() {
        return "OrderHistory{" +
                "id=" + id +
                ", changeDate=" + changeDate +
                ", oldStatus='" + oldStatus + '\'' +
                ", newStatus='" + newStatus + '\'' +
                ", changedBy='" + changedBy + '\'' +
                ", fieldChanged='" + fieldChanged + '\'' +
                ", changeDescription='" + changeDescription + '\'' +
                ", repairOrder=" + (repairOrder != null ? repairOrder.getId() : "null") +
                '}';
    }
}
