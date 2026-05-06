package com.oop.Services;

import com.oop.Model.User;

public class PasswordAuthenticator implements Authenticator {
    @Override
    public boolean authenticate(User user, String password, String otp) {
        if (user == null || password == null) {
            return false;
        }
        return password.equals(user.getPassword());
    }
}
