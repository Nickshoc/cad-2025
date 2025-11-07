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
    private boolean completed;
    private LocalDate creationDate;
    private LocalDate deadline;

    public RepairOrder() {
        this.id = generateId();
        this.creationDate = LocalDate.now();
        this.completed = false;
    }

    public RepairOrder(String deviceType, String deviceModel, String problemDescription,
                       String clientName, String clientPhone, LocalDate deadline) {
        this.id = generateId();
        this.deviceType = deviceType;
        this.deviceModel = deviceModel;
        this.problemDescription = problemDescription;
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.creationDate = LocalDate.now();
        this.deadline = deadline;
        this.completed = false;
    }

    private synchronized Long generateId() {
        return nextId++;
    }

    // Геттеры и сеттеры
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

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDate getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    @Override
    public String toString() {
        return "RepairOrder{" +
                "id=" + id +
                ", deviceType='" + deviceType + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                ", problemDescription='" + problemDescription + '\'' +
                ", clientName='" + clientName + '\'' +
                ", clientPhone='" + clientPhone + '\'' +
                ", completed=" + completed +
                ", creationDate=" + creationDate +
                ", deadline=" + deadline +
                '}';
    }
}