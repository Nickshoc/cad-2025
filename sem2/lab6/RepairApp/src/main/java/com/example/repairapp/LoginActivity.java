package com.example.repairapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView tvError;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);

        OkHttpClient client = new OkHttpClient.Builder()
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

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    tvError.setText("Заполните все поля");
                    tvError.setVisibility(View.VISIBLE);
                    return;
                }

                loginUser(username, password);
            }
        });
    }

    private void loginUser(final String username, final String password) {
        progressBar.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);

        String credentials = username + ":" + password;
        final String authHeader = "Basic " + Base64.encodeToString(
                credentials.getBytes(),
                Base64.NO_WRAP
        );

        Call<UserRoleResponse> call = apiService.getUserRole(authHeader);

        call.enqueue(new Callback<UserRoleResponse>() {
            @Override
            public void onResponse(Call<UserRoleResponse> call, Response<UserRoleResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    UserRoleResponse userInfo = response.body();

                    SharedPrefManager.getInstance(LoginActivity.this).saveUser(
                            username,
                            password,
                            userInfo.getRole() != null ? userInfo.getRole() : "USER"
                    );

                    Intent intent = new Intent(LoginActivity.this, OrdersActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    tvError.setText("Неверные логин или пароль");
                    tvError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<UserRoleResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                tvError.setText("Ошибка подключения: " + t.getMessage());
                tvError.setVisibility(View.VISIBLE);
                Toast.makeText(LoginActivity.this,
                        "Проверьте подключение к серверу",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}