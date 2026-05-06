package com.oop.Controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.oop.Model.User;
import com.oop.Model.UserRole;
import com.oop.Services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String adminLoginPage() {
        return "admin_login";
    }

    @PostMapping("/login")
    public String adminLogin(
        @RequestParam("username") String usernameOrEmail,
        @RequestParam("password") String password,
        @RequestParam("otp") String otp,
        HttpSession session,
        Model model
    ) {
        java.util.Optional<User> userOpt = userService.login(usernameOrEmail, password, otp);
        if (userOpt.isPresent() && userOpt.get().getRole() == UserRole.ADMIN) {
            User user = userOpt.get();
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("email", user.getEmail());
            session.setAttribute("fullName", user.getFullName());
            session.setAttribute("membershipType", user.getMembershipType());
            session.setAttribute("userRole", user.getRole());
            return "redirect:/admin/list";
        }
        model.addAttribute("error", true);
        return "admin_login";
    }

    @GetMapping("/list")
    public String listUsers(HttpSession session, Model model) {
        Object userId = session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login.html";
        }
        List<User> users = userService.searchUsers(null);
        List<UserSummary> summaries = users.stream()
            .map(UserSummary::fromUser)
            .collect(Collectors.toList());
        model.addAttribute("users", summaries);
        return "admin_list";
    }

    @GetMapping("/users/search")
    @ResponseBody
    public List<UserSummary> search(@RequestParam(value = "query", required = false) String query) {
        return userService.searchUsers(query)
            .stream()
            .map(UserSummary::fromUser)
            .collect(Collectors.toList());
    }

    @PostMapping("/users/delete")
    @ResponseBody
    public void delete(@RequestParam("id") Long id) {
        userService.deleteUser(id);
    }

    static class UserSummary {
        private final Long id;
        private final String username;
        private final String email;
        private final String membershipType;

        private UserSummary(Long id, String username, String email, String membershipType) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.membershipType = membershipType;
        }

        public static UserSummary fromUser(User user) {
            return new UserSummary(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getMembershipType().name()
            );
        }

        public Long getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }

        public String getMembershipType() {
            return membershipType;
        }
    }
}