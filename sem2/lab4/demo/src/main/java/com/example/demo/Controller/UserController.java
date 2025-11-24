package com.example.demo.Controller;

import com.example.demo.Service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

@Controller
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // Форма регистрации нового пользователя
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new com.example.demo.User());
        return "register";
    }

    // Создание нового пользователя
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") com.example.demo.User user,
                               @RequestParam String confirmPassword,
                               Model model) {

        try {
            // Проверка совпадения паролей
            if (!user.getPassword().equals(confirmPassword)) {
                model.addAttribute("error", "Пароли не совпадают");
                model.addAttribute("user", user);
                return "register";
            }

            // Проверка существования пользователя
            if (userService.getUserByUsername(user.getUsername()).isPresent()) {
                model.addAttribute("error", "Пользователь с таким именем уже существует");
                model.addAttribute("user", user);
                return "register";
            }

            // Проверка email
            if (userService.getUserByEmail(user.getEmail()).isPresent()) {
                model.addAttribute("error", "Пользователь с таким email уже существует");
                model.addAttribute("user", user);
                return "register";
            }

            // Шифруем пароль и сохраняем пользователя
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole("USER"); // По умолчанию роль USER

            com.example.demo.User savedUser = userService.addUser(user);

            model.addAttribute("success", "Пользователь " + savedUser.getUsername() + " успешно зарегистрирован. Теперь вы можете войти.");
            return "login";

        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "Ошибка при создании пользователя. Возможно, такое имя пользователя или email уже существуют.");
            model.addAttribute("user", user);
            return "register";
        } catch (Exception e) {
            model.addAttribute("error", "Произошла ошибка при регистрации: " + e.getMessage());
            model.addAttribute("user", user);
            return "register";
        }
    }

    // Просмотр профиля пользователя
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String viewProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        com.example.demo.User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        model.addAttribute("user", user);
        return "profile";
    }

    // Форма редактирования профиля
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile/edit")
    public String editProfileForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        com.example.demo.User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        model.addAttribute("user", user);
        return "edit-profile";
    }

    // Обновление профиля
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("user") com.example.demo.User updatedUser,
                                @RequestParam(required = false) String newPassword,
                                @RequestParam(required = false) String confirmPassword,
                                Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        com.example.demo.User existingUser = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Обновляем только разрешенные поля
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setFullName(updatedUser.getFullName());

        // Обновляем пароль, если указан новый
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("error", "Пароли не совпадают");
                model.addAttribute("user", existingUser);
                return "edit-profile";
            }
            existingUser.setPassword(passwordEncoder.encode(newPassword));
        }

        userService.updateUser(existingUser);

        model.addAttribute("success", "Профиль успешно обновлен");
        model.addAttribute("user", existingUser);
        return "profile";
    }

    // Список всех пользователей (только для администраторов)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users")
    public String listUsers(Model model) {
        List<com.example.demo.User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "user-list";
    }
}