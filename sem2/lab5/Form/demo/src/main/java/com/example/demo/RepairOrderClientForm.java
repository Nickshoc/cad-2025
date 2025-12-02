package com.example.demo;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class RepairOrderClientForm extends JFrame {
    private JList<String> orderList;
    private DefaultListModel<String> listModel;
    private String authHeader;
    private String currentUser;
    private String userRole;
    private JButton deleteButton;
    private JButton refreshButton;
    private List<RepairOrderDTO> orders = new ArrayList<>();
    private Gson gson = new Gson();

    class RepairOrderDTO {
        private Long id;
        private String deviceType;
        private String deviceModel;
        private String problemDescription;
        private String clientName;
        private String clientPhone;
        private String status;
        private String operatorComment;
        private String creationDate;
        private String deadline;
        private String createdBy;
        private String createdByUsername; // Новое поле

        public RepairOrderDTO() {}

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
        public String getCreationDate() { return creationDate; }
        public void setCreationDate(String creationDate) { this.creationDate = creationDate; }
        public String getDeadline() { return deadline; }
        public void setDeadline(String deadline) { this.deadline = deadline; }
        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
        public String getCreatedByUsername() { return createdByUsername; }
        public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }

        @Override
        public String toString() {
            String creator = createdByUsername != null ? createdByUsername : createdBy;
            return String.format("[ID: %d] %s %s - %s (Статус: %s, Создал: %s)",
                    id, deviceType, deviceModel, clientName, status, creator);
        }
    }

    public RepairOrderClientForm(String authHeader, String username, String role) {
        this.authHeader = authHeader;
        this.currentUser = username;
        this.userRole = role;

        setTitle("Управление заявками на ремонт - " + username + " [" + role + "]");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel(new FlowLayout());
        refreshButton = new JButton("Обновить");
        deleteButton = new JButton("Удалить выбранную заявку");

        JButton logoutButton = new JButton("Выйти");

        deleteButton.setVisible("ADMIN".equals(userRole));

        controlPanel.add(refreshButton);
        controlPanel.add(deleteButton);
        controlPanel.add(logoutButton);
        add(controlPanel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        orderList = new JList<>(listModel);
        orderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(orderList), BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(new JLabel("Двойной клик по заявке для просмотра деталей. Роль: " + userRole), BorderLayout.WEST);
        add(infoPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> loadOrders());

        deleteButton.addActionListener(e -> {
            int selectedIndex = orderList.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < orders.size()) {
                RepairOrderDTO selectedOrder = orders.get(selectedIndex);
                deleteOrder(selectedOrder.getId());
            }
        });

        orderList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = orderList.locationToIndex(evt.getPoint());
                    if (index >= 0 && index < orders.size()) {
                        showOrderDetails(orders.get(index));
                    }
                }
            }
        });

        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Вы уверены, что хотите выйти из системы?",
                    "Подтверждение выхода",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Закрываем текущее окно
                dispose();

                // Открываем окно авторизации
                SwingUtilities.invokeLater(() -> {
                    LoginForm loginForm = new LoginForm();
                    loginForm.setVisible(true);
                });
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                int confirm = JOptionPane.showConfirmDialog(
                        RepairOrderClientForm.this,
                        "Закрыть приложение?",
                        "Подтверждение",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        loadOrders();
    }

    private void loadOrders() {
        new Thread(() -> {
            try {
                System.out.println("=== LOAD ORDERS STARTED ===");
                System.out.println("User: " + currentUser + ", Role: " + userRole);

                URL url = new URL("http://localhost:8080/api/orders");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Basic " + authHeader);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
                conn.setRequestProperty("Pragma", "no-cache");
                conn.setRequestProperty("Expires", "0");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                System.out.println("HTTP Response Code: " + responseCode);

                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    String jsonResponse = response.toString();
                    System.out.println("JSON Response length: " + jsonResponse.length());

                    if (!jsonResponse.isEmpty()) {
                        System.out.println("JSON starts with: " +
                                (jsonResponse.length() > 100 ? jsonResponse.substring(0, 100) : jsonResponse));
                    }

                    List<RepairOrderDTO> newOrders = gson.fromJson(jsonResponse,
                            new TypeToken<List<RepairOrderDTO>>(){}.getType());

                    System.out.println("Parsed " + (newOrders != null ? newOrders.size() : 0) + " orders");

                    synchronized (this) {
                        orders.clear();
                        if (newOrders != null) {
                            orders.addAll(newOrders);
                        }
                    }

                    SwingUtilities.invokeLater(() -> {
                        listModel.clear();
                        if (newOrders != null) {
                            for (RepairOrderDTO order : newOrders) {
                                if (order != null && order.getId() != null) {
                                    listModel.addElement(order.toString());
                                }
                            }
                        }
                        System.out.println("UI updated. Displaying " + listModel.size() + " orders");

                        setTitle("Управление заявками на ремонт - " + currentUser +
                                " [" + userRole + "] - Заявок: " + listModel.size());

                        revalidate();
                        repaint();
                    });

                } else {
                    StringBuilder errorResponse = new StringBuilder();
                    try {
                        BufferedReader errorReader = new BufferedReader(
                                new InputStreamReader(conn.getErrorStream(), "UTF-8"));
                        String errorLine;
                        while ((errorLine = errorReader.readLine()) != null) {
                            errorResponse.append(errorLine);
                        }
                        errorReader.close();
                    } catch (Exception e) {
                        errorResponse.append("Не удалось прочитать сообщение об ошибке: ").append(e.getMessage());
                    }

                    final String errorMsg = errorResponse.toString();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(RepairOrderClientForm.this,
                                "Ошибка загрузки заявок. Код: " + responseCode +
                                        "\nСообщение: " + errorMsg,
                                "Ошибка", JOptionPane.ERROR_MESSAGE);
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(RepairOrderClientForm.this,
                            "Ошибка подключения к серверу: " + e.getMessage(),
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                });
            }
            System.out.println("=== LOAD ORDERS FINISHED ===");
        }).start();
    }

    private void deleteOrder(Long orderId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Вы уверены, что хотите удалить заявку ID: " + orderId + "?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            new Thread(() -> {
                try {
                    System.out.println("=== DELETE ORDER " + orderId + " ===");

                    URL url = new URL("http://localhost:8080/api/orders/" + orderId);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("DELETE");
                    conn.setRequestProperty("Authorization", "Basic " + authHeader);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Cache-Control", "no-cache");
                    conn.setConnectTimeout(3000);
                    conn.setReadTimeout(3000);

                    int responseCode = conn.getResponseCode();
                    System.out.println("Delete response code: " + responseCode);

                    // Читаем ответ
                    StringBuilder response = new StringBuilder();
                    if (conn.getInputStream() != null) {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();
                    }

                    SwingUtilities.invokeLater(() -> {
                        if (responseCode == 200) {
                            JOptionPane.showMessageDialog(RepairOrderClientForm.this,
                                    "Заявка успешно удалена",
                                    "Успех", JOptionPane.INFORMATION_MESSAGE);

                            // Удаляем из локального списка
                            synchronized (this) {
                                orders.removeIf(order -> orderId.equals(order.getId()));
                            }

                            // Обновляем UI
                            listModel.clear();
                            for (RepairOrderDTO order : orders) {
                                listModel.addElement(order.toString());
                            }

                            System.out.println("Order " + orderId + " removed locally. Remaining: " + orders.size());

                        } else if (responseCode == 403) {
                            JOptionPane.showMessageDialog(RepairOrderClientForm.this,
                                    "Отказано в доступе. Только администраторы могут удалять заявки.",
                                    "Ошибка доступа", JOptionPane.ERROR_MESSAGE);
                        } else if (responseCode == 404) {
                            JOptionPane.showMessageDialog(RepairOrderClientForm.this,
                                    "Заявка не найдена",
                                    "Ошибка", JOptionPane.ERROR_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(RepairOrderClientForm.this,
                                    "Ошибка удаления. Код: " + responseCode +
                                            "\nОтвет: " + response.toString(),
                                    "Ошибка", JOptionPane.ERROR_MESSAGE);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(RepairOrderClientForm.this,
                                "Ошибка подключения: " + e.getMessage(),
                                "Ошибка", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();
        }
    }

    private void showOrderDetails(RepairOrderDTO order) {
        if (order == null) return;

        String details = String.format(
                "Детали заявки:\n\n" +
                        "ID: %d\n" +
                        "Тип устройства: %s\n" +
                        "Модель: %s\n" +
                        "Клиент: %s\n" +
                        "Статус: %s\n" +
                        "Создал: %s",
                order.getId(),
                order.getDeviceType(),
                order.getDeviceModel(),
                order.getClientName(),
                order.getStatus(),
                order.getCreatedBy()
        );

        JOptionPane.showMessageDialog(this, details, "Детали заявки", JOptionPane.INFORMATION_MESSAGE);
    }
}
