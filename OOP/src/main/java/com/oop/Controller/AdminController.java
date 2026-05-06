package com.oop.Controller;

import com.oop.Model.User;
import com.oop.Services.UserService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/users")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/search")
    @ResponseBody
    public List<UserSummary> search(@RequestParam(value = "query", required = false) String query) {
        return userService.searchUsers(query)
            .stream()
            .map(UserSummary::fromUser)
            .collect(Collectors.toList());
    }

    @PostMapping("/delete")
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
