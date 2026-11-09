package com.vehiclerental.service;

import com.vehiclerental.model.User;
import com.vehiclerental.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Checks a login against the users table.
 * The typed password is compared with the saved BCrypt hash - the real password is never stored.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByEmailIgnoreCase(email.trim())
                .filter(User::isActive)                                         // deactivated accounts cannot log in
                .filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }
}