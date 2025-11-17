package com.example.demo;

import java.time.LocalDate;

public class RepairOrder {
    private static Long nextId = 1L;
    private Long id;
    private String deviceType;
    private String deviceModel;
    private String problemDescription;
    private String clientName;
    private String clientPhone;
    private String status;
    private String operatorComment;
    private LocalDate creationDate;
    private LocalDate deadline;
    private String createdBy;

    public RepairOrder() {
        this.id = generateId();
        this.creationDate = LocalDate.now();
        this.status = "NEW";
    }

    public RepairOrder(String deviceType, String deviceModel, String problemDescription,
                       String clientName, String clientPhone, LocalDate deadline, String createdBy) {
        this.id = generateId();
        this.deviceType = deviceType;
        this.deviceModel = deviceModel;
        this.problemDescription = problemDescription;
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.creationDate = LocalDate.now();
        this.deadline = deadline;
        this.status = "NEW";
        this.createdBy = createdBy;
    }

    private synchronized Long generateId() {
        return nextId++;
    }

    public Long getId() { return id; }

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
                '}';
    }
}