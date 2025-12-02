package com.example.demo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class RepairOrderResponseDTO {
    private Long id;
    private String deviceType;
    private String deviceModel;
    private String problemDescription;
    private String clientName;
    private String clientPhone;
    private String status;
    private String operatorComment;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate creationDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;

    private String createdBy;
    private String createdByUsername; // Имя пользователя, создавшего заказ

    public RepairOrderResponseDTO() {}

    public RepairOrderResponseDTO(RepairOrder order) {
        this.id = order.getId();
        this.deviceType = order.getDeviceType();
        this.deviceModel = order.getDeviceModel();
        this.problemDescription = order.getProblemDescription();
        this.clientName = order.getClientName();
        this.clientPhone = order.getClientPhone();
        this.status = order.getStatus();
        this.operatorComment = order.getOperatorComment();
        this.creationDate = order.getCreationDate();
        this.deadline = order.getDeadline();
        this.createdBy = order.getCreatedBy();
        this.createdByUsername = order.getUser() != null ? order.getUser().getUsername() : null;
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
    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }
}