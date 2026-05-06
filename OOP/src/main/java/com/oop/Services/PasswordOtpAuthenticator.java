package com.oop.Services;

import com.oop.Model.AdminUser;
import com.oop.Model.User;

public class PasswordOtpAuthenticator implements Authenticator {
    @Override
    public boolean authenticate(User user, String password, String otp) {
        if (!(user instanceof AdminUser)) {
            return false;
        }
        if (password == null || otp == null) {
            return false;
        }
        AdminUser admin = (AdminUser) user;
        return password.equals(admin.getPassword()) && otp.equals(admin.getOtpSecret());
    }
}
