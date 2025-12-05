package com.example.repairapp;

import android.util.Base64;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class AuthInterceptor implements Interceptor {

    private String username;
    private String password;

    public AuthInterceptor(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        String credentials = username + ":" + password;
        String authHeader = "Basic " + Base64.encodeToString(
                credentials.getBytes(),
                Base64.NO_WRAP
        );

        Request newRequest = originalRequest.newBuilder()
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .build();

        return chain.proceed(newRequest);
    }
}
