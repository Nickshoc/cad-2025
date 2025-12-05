package com.example.repairapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

public class OrdersActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private TextView tvUserRole;
    private Button btnLogout;
    private RecyclerView rvOrders;
    private ProgressBar progressBar;

    private ApiService apiService;
    private OrderAdapter adapter;
    private List<RepairOrder> ordersList = new ArrayList<>();
    private SharedPrefManager.User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        currentUser = SharedPrefManager.getInstance(this).getUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        tvWelcome = findViewById(R.id.tvWelcome);
        tvUserRole = findViewById(R.id.tvUserRole);
        btnLogout = findViewById(R.id.btnLogout);
        rvOrders = findViewById(R.id.rvOrders);
        progressBar = findViewById(R.id.progressBar);

        tvWelcome.setText("Добро пожаловать, " + currentUser.getUsername());
        String roleText = getRoleDisplayText(currentUser.getRole());
        tvUserRole.setText(roleText);

        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(ordersList, new OrderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RepairOrder order) {
                showOrderDetails(order);
            }

            @Override
            public void onDeleteClick(RepairOrder order) {
                deleteOrder(order.getId());
            }
        }, currentUser.getRole());
        rvOrders.setAdapter(adapter);

        setupRetrofit();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(OrdersActivity.this).clear();
                startActivity(new Intent(OrdersActivity.this, LoginActivity.class));
                finish();
            }
        });

        loadOrders();
    }

    private String getRoleDisplayText(String role) {
        switch (role) {
            case "ADMIN":
                return "Администратор";
            case "OPERATOR":
                return "Оператор";
            case "USER":
                return "Пользователь";
            default:
                return "Неизвестно";
        }
    }

    private void setupRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(currentUser.getUsername(), currentUser.getPassword()))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    private void showOrderDetails(RepairOrder order) {
        String message = "Заявка #" + order.getId() +
                "\nУстройство: " + order.getDeviceType() + " " + order.getDeviceModel() +
                "\nСтатус: " + order.getStatus() +
                "\nКлиент: " + order.getClientName() +
                "\nТелефон: " + order.getClientPhone();

        if (currentUser.getRole().equals("ADMIN")) {
            showAdminOptionsDialog(order);
        } else {
            showOrderInfoDialog(order, message);
        }
    }

    private void showOrderInfoDialog(RepairOrder order, String message) {
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(this);

        builder.setTitle("Заявка #" + order.getId())
                .setMessage(message)
                .setPositiveButton("Закрыть", null)
                .create()
                .show();
    }

    private void showAdminOptionsDialog(final RepairOrder order) {
        String message = "Устройство: " + order.getDeviceType() + " " + order.getDeviceModel() +
                "\nКлиент: " + order.getClientName() +
                "\nСтатус: " + order.getStatus() +
                "\nСоздана: " + order.getCreationDate() +
                "\nСрок: " + order.getDeadline();

        if (order.getProblemDescription() != null && !order.getProblemDescription().isEmpty()) {
            message += "\nОписание: " + order.getProblemDescription();
        }

        if (order.getOperatorComment() != null && !order.getOperatorComment().isEmpty()) {
            message += "\nКомментарий оператора: " + order.getOperatorComment();
        }

        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(this);

        builder.setTitle("Заявка #" + order.getId())
                .setMessage(message)
                .setPositiveButton("Закрыть", null)
                .setNegativeButton("Удалить", (dialog, which) -> {
                    deleteOrder(order.getId());
                });

        builder.create().show();
    }

    private void deleteOrder(Long orderId) {
        androidx.appcompat.app.AlertDialog.Builder confirmBuilder =
                new androidx.appcompat.app.AlertDialog.Builder(this);

        confirmBuilder.setTitle("Подтверждение")
                .setMessage("Вы уверены, что хотите удалить эту заявку?")
                .setPositiveButton("Да", (dialog, which) -> {
                    performDeleteOrder(orderId);
                })
                .setNegativeButton("Нет", null)
                .create()
                .show();
    }

    private void performDeleteOrder(Long orderId) {
        progressBar.setVisibility(View.VISIBLE);

        Call<Void> call = apiService.deleteOrder(orderId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Toast.makeText(OrdersActivity.this,
                            "Заявка удалена",
                            Toast.LENGTH_SHORT).show();
                    loadOrders(); // Обновляем список
                } else {
                    Toast.makeText(OrdersActivity.this,
                            "Ошибка удаления заявки",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrdersActivity.this,
                        "Ошибка подключения: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }

    private void loadOrders() {
        progressBar.setVisibility(View.VISIBLE);

        Call<List<RepairOrder>> call = apiService.getOrders();

        call.enqueue(new Callback<List<RepairOrder>>() {
            @Override
            public void onResponse(Call<List<RepairOrder>> call, Response<List<RepairOrder>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    ordersList.clear();
                    ordersList.addAll(response.body());
                    adapter.updateOrders(ordersList);

                    Toast.makeText(OrdersActivity.this,
                            "Загружено заявок: " + ordersList.size(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OrdersActivity.this,
                            "Ошибка загрузки заявок",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<RepairOrder>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrdersActivity.this,
                        "Ошибка подключения: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}