package com.vehiclerental.auth;

import com.vehiclerental.model.User;

import java.util.Optional;

/**
 * Login rules. The controller only knows this interface.
 * OOP: Abstraction.
 */
public interface AuthService {

    /** Returns the user if the email and password are correct and the account is active. */
    Optional<User> login(String email, String password);
}
