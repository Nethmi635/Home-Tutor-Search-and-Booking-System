package com.oop.Services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.oop.Model.AdminUser;
import com.oop.Model.MembershipType;
import com.oop.Model.RegularUser;
import com.oop.Model.User;
import com.oop.Repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final Authenticator regularAuthenticator = new PasswordAuthenticator();
    private final Authenticator adminAuthenticator = new PasswordOtpAuthenticator();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String fullName, String username, String email, String password, String membershipLabel) {
        validateRequired(fullName, "Full name");
        validateRequired(username, "Username");
        validateRequired(email, "Email");
        validateRequired(password, "Password");

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists.");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists.");
        }

        RegularUser user = new RegularUser();
        user.setFullName(fullName);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setMembershipType(MembershipType.fromLabel(membershipLabel));
        return userRepository.save(user);
    }

    public Optional<User> login(String usernameOrEmail, String password, String otp) {
        Optional<User> userOpt = userRepository.findByUsername(usernameOrEmail);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(usernameOrEmail);
        }
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();
        Authenticator authenticator = selectAuthenticator(user); // Polymorphism: interface call selects behavior.
        if (authenticator.authenticate(user, password, otp)) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public Optional<User> updateProfile(String username, String email, String password, String membershipLabel) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();

        if (email != null && !email.isBlank()) {
            Optional<User> existing = userRepository.findByEmail(email);
            if (existing.isPresent() && !existing.get().getId().equals(user.getId())) {
                throw new IllegalArgumentException("Email already exists.");
            }
            user.setEmail(email);
        }

        if (password != null && !password.isBlank()) {
            user.setPassword(password);
        }

        if (membershipLabel != null && !membershipLabel.isBlank()) {
            user.setMembershipType(MembershipType.fromLabel(membershipLabel));
        }

        return Optional.of(userRepository.save(user));
    }

    public Optional<User> updateProfile(Long userId, String username, String email, String password, String membershipLabel) {
        if (userId == null) {
            return Optional.empty();
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();

        validateRequired(username, "Username");
        validateRequired(email, "Email");

        Optional<User> usernameMatch = userRepository.findByUsername(username);
        if (usernameMatch.isPresent() && !usernameMatch.get().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Username already exists.");
        }

        Optional<User> emailMatch = userRepository.findByEmail(email);
        if (emailMatch.isPresent() && !emailMatch.get().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Email already exists.");
        }

        user.setUsername(username);
        user.setEmail(email);

        if (password != null && !password.isBlank()) {
            user.setPassword(password);
        }

        if (membershipLabel != null && !membershipLabel.isBlank()) {
            user.setMembershipType(MembershipType.fromLabel(membershipLabel));
        }

        return Optional.of(userRepository.save(user));
    }

    public Optional<User> findById(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }
        return userRepository.findById(userId);
    }

    public List<User> searchUsers(String query) {
        if (query == null || query.isBlank()) {
            return userRepository.findAll();
        }
        String trimmed = query.trim();
        if (isNumeric(trimmed)) {
            Optional<User> byId = userRepository.findById(Long.parseLong(trimmed));
            return byId.map(List::of).orElse(Collections.emptyList());
        }
        Optional<User> byUsername = userRepository.findByUsername(trimmed);
        return byUsername.map(List::of).orElse(Collections.emptyList());
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private Authenticator selectAuthenticator(User user) {
        if (user instanceof AdminUser) {
            return adminAuthenticator;
        }
        return regularAuthenticator;
    }

    private void validateRequired(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " is required.");
        }
    }

    private boolean isNumeric(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
