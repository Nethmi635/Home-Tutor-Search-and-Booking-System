package com.oop.Controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.oop.Model.User;
import com.oop.Model.UserRole;
import com.oop.Services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
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
            return "redirect:/users/login?registered=1";
        } catch (IllegalArgumentException ex) {
            return "redirect:/users/register?error=1";
        }
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "registered", required = false) String registered,
                           Model model) {
        if (registered != null) {
            model.addAttribute("registered", true);
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(
        @RequestParam("username") String usernameOrEmail,
        @RequestParam("password") String password,
        @RequestParam(value = "otp", required = false) String otp,
        HttpSession session,
        Model model
    ) {
        Optional<User> user = userService.login(usernameOrEmail, password, otp);
        if (user.isPresent()) {
            session.setAttribute("userId", user.get().getId());
            session.setAttribute("username", user.get().getUsername());
            session.setAttribute("email", user.get().getEmail());
            session.setAttribute("fullName", user.get().getFullName());
            session.setAttribute("membershipType", user.get().getMembershipType());
            session.setAttribute("userRole", user.get().getRole());

            return user.get().getRole() == UserRole.ADMIN
                ? "redirect:/admin/list"
                : "redirect:/users/dashboard";
        }
        model.addAttribute("error", true);
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Object userId = session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/login";
        }
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("email", session.getAttribute("email"));
        model.addAttribute("fullName", session.getAttribute("fullName"));
        model.addAttribute("membershipType", session.getAttribute("membershipType"));
        return "dashboard";
    }

    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/users/login";
        }

        model.addAttribute("userId", userId);
        userService.findById(userId).ifPresentOrElse(user -> {
            model.addAttribute("username", user.getUsername());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("fullName", user.getFullName());
            model.addAttribute("membershipType", user.getMembershipType());
        }, () -> {
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("email", session.getAttribute("email"));
            model.addAttribute("fullName", session.getAttribute("fullName"));
            model.addAttribute("membershipType", session.getAttribute("membershipType"));
        });

        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
        @RequestParam("userId") Long userId,
        @RequestParam("username") String username,
        @RequestParam("email") String email,
        @RequestParam("fullName") String fullName,
        @RequestParam(value = "password", required = false) String password,
        @RequestParam("membership") String membership,
        HttpSession session
    ) {
        try {
            Optional<User> updated = userService.updateProfile(userId, username, fullName, email, password, membership);
            if (updated.isPresent()) {
                session.setAttribute("userId", updated.get().getId());
                session.setAttribute("username", updated.get().getUsername());
                session.setAttribute("email", updated.get().getEmail());
                session.setAttribute("fullName", updated.get().getFullName());
                session.setAttribute("membershipType", updated.get().getMembershipType());
                return "redirect:/users/dashboard?updated=1";
            }
            return "redirect:/users/profile?error=1";
        } catch (IllegalArgumentException ex) {
            return "redirect:/users/profile?error=1";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}