package com.oop.Services;

import com.oop.Model.User;

// Polymorphism: different authenticators implement this interface.
public interface Authenticator {
    boolean authenticate(User user, String password, String otp);
}
