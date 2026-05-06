package com.oop.Model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

// Inheritance: AdminUser extends User and shares its base fields.
@Entity
@DiscriminatorValue("ADMIN")
public class AdminUser extends User {

    @Column(nullable = true, length = 32)
    private String otpSecret = "123456";

    public AdminUser() {
        setRole(UserRole.ADMIN);
    }

    public String getOtpSecret() {
        return otpSecret;
    }

    public void setOtpSecret(String otpSecret) {
        this.otpSecret = otpSecret;
    }
}
