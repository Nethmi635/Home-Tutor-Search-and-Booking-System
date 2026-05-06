package com.oop.Controller;

import com.oop.Model.User;
import com.oop.Model.UserRole;
import com.oop.Services.UserService;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String register(
        @RequestParam("fullname") String fullName,
        @RequestParam("username") String username,
        @RequestParam("email") String email,
        @RequestParam("password") String password,
        @RequestParam("membership") String membership
    ) {
        try {
            userService.registerUser(fullName, username, email, password, membership);
            return "redirect:/login.html?registered=1";
        } catch (IllegalArgumentException ex) {
            return "redirect:/register.html?error=1";
        }
    }

    @PostMapping("/login")
    public String login(
        @RequestParam("username") String usernameOrEmail,
        @RequestParam("password") String password,
        @RequestParam(value = "otp", required = false) String otp
    ) {
        Optional<User> user = userService.login(usernameOrEmail, password, otp);
        if (user.isPresent()) {
            return user.get().getRole() == UserRole.ADMIN
                ? "redirect:/admin_list.html"
                : "redirect:/dashboard.html";
        }
        return "redirect:/login.html?error=1";
    }

    @PostMapping("/profile")
    public String updateProfile(
        @RequestParam("username") String username,
        @RequestParam("email") String email,
        @RequestParam(value = "password", required = false) String password,
        @RequestParam("membership") String membership
    ) {
        try {
            Optional<User> updated = userService.updateProfile(username, email, password, membership);
            return updated.isPresent()
                ? "redirect:/profile.html?updated=1"
                : "redirect:/profile.html?error=1";
        } catch (IllegalArgumentException ex) {
            return "redirect:/profile.html?error=1";
        }
    }
}
