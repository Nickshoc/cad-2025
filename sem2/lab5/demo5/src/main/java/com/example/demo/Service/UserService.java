package com.example.demo.Service;

import com.example.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public com.example.demo.User addUser(com.example.demo.User user) {
        return userRepository.save(user);
    }

    public List<com.example.demo.User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<com.example.demo.User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<com.example.demo.User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<com.example.demo.User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public com.example.demo.User updateUser(com.example.demo.User user) {
        return userRepository.save(user);
    }


}