package com.example.smsw.controller;

import com.example.smsw.entity.Admin;
import com.example.smsw.repository.AdminRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminController {

    private final AdminRepository adminRepository;

    public AdminController(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @GetMapping("/change-password")
    public String changePasswordPage(HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/";
        }

        return "change_password";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,
            Model model) {

        String username =
                (String) session.getAttribute("user");

        Admin admin =
                adminRepository.findByUsername(username);

        if (admin == null) {
            return "redirect:/";
        }

        if (!admin.getPassword().equals(oldPassword)) {

            model.addAttribute(
                    "error",
                    "Old password is incorrect");

            return "change_password";
        }

        if (!newPassword.equals(confirmPassword)) {

            model.addAttribute(
                    "error",
                    "Passwords do not match");

            return "change_password";
        }

        admin.setPassword(newPassword);

        adminRepository.save(admin);

        model.addAttribute(
                "success",
                "Password changed successfully");

        return "change_password";
    }
}