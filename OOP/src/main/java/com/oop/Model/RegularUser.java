package com.oop.Model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

// Inheritance: RegularUser extends User and shares its base fields.
@Entity
@DiscriminatorValue("REGULAR")
public class RegularUser extends User {

    public RegularUser() {
        setRole(UserRole.REGULAR);
    }
}
