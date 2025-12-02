package com.example.demo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Base64;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginForm() {
        setTitle("Авторизация - Система ремонта");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 10, 10));
        setLocationRelativeTo(null); // Центрировать окно на экране

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Войти");

        add(new JLabel("Логин:"));
        add(usernameField);
        add(new JLabel("Пароль:"));
        add(passwordField);
        add(new JPanel());
        add(new JPanel());
        add(new JPanel());
        add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginForm.this,
                            "Заполните все поля", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String encodedAuth = Base64.getEncoder()
                        .encodeToString((username + ":" + password).getBytes());

                // Проверяем доступ к API
                boolean authSuccess = testAuth(encodedAuth);

                if (authSuccess) {
                    // Проверяем роль пользователя
                    String role = getUserRole(encodedAuth);
                    RepairOrderClientForm mainForm = new RepairOrderClientForm(encodedAuth, username, role);
                    mainForm.setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginForm.this,
                            "Неверный логин или пароль", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private boolean testAuth(String authHeader) {
        try {
            java.net.URL url = new java.net.URL("http://localhost:8080/api/orders");
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Basic " + authHeader);
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(3000);

            int responseCode = conn.getResponseCode();
            System.out.println("Auth Test Response Code: " + responseCode);

            // Читаем ответ В ЛЮБОМ СЛУЧАЕ
            java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(
                            conn.getInputStream(), // Всегда читаем input stream
                            "UTF-8"
                    )
            );

            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String responseStr = response.toString().trim();
            System.out.println("Full Response (first 500 chars): " +
                    (responseStr.length() > 500 ? responseStr.substring(0, 500) + "..." : responseStr));

            // Проверяем, что это JSON массив
            if (responseCode == 200) {
                boolean isJsonArray = responseStr.startsWith("[") && responseStr.endsWith("]");
                System.out.println("Is JSON array: " + isJsonArray);
                return isJsonArray;
            }

            return false;

        } catch (Exception e) {
            System.err.println("Auth test error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String getUserRole(String authHeader) {
        try {
            System.out.println("=== DETERMINING USER ROLE ===");

            try {
                java.net.URL url = new java.net.URL("http://localhost:8080/api/user/role");
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Basic " + authHeader);
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(3000);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    java.io.BufferedReader reader = new java.io.BufferedReader(
                            new java.io.InputStreamReader(conn.getInputStream(), "UTF-8")
                    );
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    com.google.gson.Gson gson = new com.google.gson.Gson();
                    java.util.Map<String, String> result = gson.fromJson(response.toString(),
                            new com.google.gson.reflect.TypeToken<java.util.Map<String, String>>(){}.getType());

                    String role = result.get("role");
                    System.out.println("Got role from API: " + role);
                    return role != null ? role : "USER";
                }
            } catch (Exception e) {
                System.out.println("API role check failed: " + e.getMessage());
            }

            java.net.URL url = new java.net.URL("http://localhost:8080/api/orders");
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Basic " + authHeader);
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(3000);

            int responseCode = conn.getResponseCode();
            System.out.println("Orders API response code: " + responseCode);

            if (responseCode == 200) {
                java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(conn.getInputStream(), "UTF-8")
                );
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                String jsonResponse = response.toString();

                com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
                com.google.gson.JsonArray jsonArray = parser.parse(jsonResponse).getAsJsonArray();
                int orderCount = jsonArray.size();

                System.out.println("User can see " + orderCount + " orders");


                boolean isAdmin = checkAdminRights(authHeader);
                if (isAdmin) {
                    System.out.println("User has ADMIN rights");
                    return "ADMIN";
                }


                System.out.println("User has USER rights");
                return "USER";
            }

            System.out.println("Defaulting to USER role");
            return "USER";

        } catch (Exception e) {
            System.err.println("Role determination error: " + e.getMessage());
            return "USER"; // По умолчанию USER
        }
    }

    private boolean checkAdminRights(String authHeader) {
        try {
            java.net.URL url = new java.net.URL("http://localhost:8080/api/orders/99999"); // Несуществующий ID
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", "Basic " + authHeader);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(2000);

            int responseCode = conn.getResponseCode();
            System.out.println("Admin check - DELETE response: " + responseCode);

            // Если не 403 - возможно ADMIN
            return responseCode != 403;
        } catch (Exception e) {
            System.out.println("Admin check failed: " + e.getMessage());
            return false;
        }
    }

    private boolean checkOperatorRights(String authHeader) {
        try {
            java.net.URL url = new java.net.URL("http://localhost:8080/api/orders");
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Basic " + authHeader);
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(2000);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(conn.getInputStream(), "UTF-8")
                );
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
                com.google.gson.JsonArray jsonArray = parser.parse(response.toString()).getAsJsonArray();

                if (jsonArray.size() > 0) {
                    long orderId = jsonArray.get(0).getAsJsonObject().get("id").getAsLong();



                    java.net.URL acceptUrl = new java.net.URL("http://localhost:8080/acceptOrder/" + orderId);
                    java.net.HttpURLConnection acceptConn = (java.net.HttpURLConnection) acceptUrl.openConnection();
                    acceptConn.setRequestMethod("POST");
                    acceptConn.setRequestProperty("Authorization", "Basic " + authHeader);
                    acceptConn.setConnectTimeout(2000);

                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.out.println("Operator check failed: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
        });
    }
}