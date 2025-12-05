package com.example.repairapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

public class SharedPrefManager {

    private static final String PREF_NAME = "repair_app_pref";
    private static final String KEY_USERNAME = "key_username";
    private static final String KEY_PASSWORD = "key_password";
    private static final String KEY_ROLE = "key_role";

    private static SharedPrefManager instance;
    private SharedPreferences sharedPreferences;

    private SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    public static class User {
        private String username;
        private String password;
        private String role;

        public User(String username, String password, String role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getRole() {
            return role;
        }
    }

    public User getUser() {
        String username = sharedPreferences.getString(KEY_USERNAME, null);
        String password = sharedPreferences.getString(KEY_PASSWORD, null);
        String role = sharedPreferences.getString(KEY_ROLE, null);

        if (username != null && password != null && role != null) {
            // Расшифровываем пароль (в реальном приложении используйте безопасное хранение)
            String decodedPassword = new String(Base64.decode(password, Base64.DEFAULT));
            return new User(username, decodedPassword, role);
        }
        return null;
    }

    public void saveUser(String username, String password, String role) {
        // Шифруем пароль в Base64 (в реальном приложении используйте безопасное хранение)
        String encodedPassword = Base64.encodeToString(password.getBytes(), Base64.DEFAULT);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, encodedPassword);
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public String getAuthHeader() {
        User user = getUser();
        if (user != null) {
            String credentials = user.getUsername() + ":" + user.getPassword();
            return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        }
        return null;
    }
}
