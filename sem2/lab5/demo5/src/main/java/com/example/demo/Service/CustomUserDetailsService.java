package com.example.demo.Service;

import com.example.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.example.demo.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

        // ДОБАВЬТЕ отладочный вывод
        System.out.println("=== Authenticating user: " + username + " ===");
        System.out.println("Stored password hash: " + user.getPassword());
        System.out.println("Role from DB: " + user.getRole());

        // ВАЖНО: Используйте authorities() вместо roles() для префикса ROLE_
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole()) // ← ИЗМЕНИТЕ ЭТУ СТРОКУ
                .build();
    }

}