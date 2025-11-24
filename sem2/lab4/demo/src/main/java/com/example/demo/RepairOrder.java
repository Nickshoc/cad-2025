package com.example.demo;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "repair_orders")
public class RepairOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_type", nullable = false)
    private String deviceType;

    @Column(name = "device_model", nullable = false)
    private String deviceModel;

    @Column(name = "problem_description", length = 1000)
    private String problemDescription;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "client_phone", nullable = false)
    private String clientPhone;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "operator_comment", length = 1000)
    private String operatorComment;

    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    @Column(name = "deadline", nullable = false)
    private LocalDate deadline;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "repairOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderHistory> orderHistories = new ArrayList<>();

    // Конструкторы
    public RepairOrder() {
        this.creationDate = LocalDate.now();
        this.status = "NEW";
    }

    public RepairOrder(String deviceType, String deviceModel, String problemDescription,
                       String clientName, String clientPhone, LocalDate deadline, User user) {
        this();
        this.deviceType = deviceType;
        this.deviceModel = deviceModel;
        this.problemDescription = problemDescription;
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.deadline = deadline;
        this.user = user;
        this.createdBy = user.getUsername();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public String getDeviceModel() { return deviceModel; }
    public void setDeviceModel(String deviceModel) { this.deviceModel = deviceModel; }

    public String getProblemDescription() { return problemDescription; }
    public void setProblemDescription(String problemDescription) { this.problemDescription = problemDescription; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientPhone() { return clientPhone; }
    public void setClientPhone(String clientPhone) { this.clientPhone = clientPhone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOperatorComment() { return operatorComment; }
    public void setOperatorComment(String operatorComment) { this.operatorComment = operatorComment; }

    public LocalDate getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public User getUser() { return user; }
    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.createdBy = user.getUsername();
        }
    }

    public List<OrderHistory> getOrderHistories() { return orderHistories; }
    public void setOrderHistories(List<OrderHistory> orderHistories) { this.orderHistories = orderHistories; }

    // Вспомогательные методы
    public void addOrderHistory(OrderHistory orderHistory) {
        orderHistories.add(orderHistory);
        orderHistory.setRepairOrder(this);
    }

    @Override
    public String toString() {
        return "RepairOrder{" +
                "id=" + id +
                ", deviceType='" + deviceType + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                ", problemDescription='" + problemDescription + '\'' +
                ", clientName='" + clientName + '\'' +
                ", clientPhone='" + clientPhone + '\'' +
                ", status='" + status + '\'' +
                ", operatorComment='" + operatorComment + '\'' +
                ", creationDate=" + creationDate +
                ", deadline=" + deadline +
                ", createdBy='" + createdBy + '\'' +
                ", user=" + (user != null ? user.getUsername() : "null") +
                '}';
    }
}