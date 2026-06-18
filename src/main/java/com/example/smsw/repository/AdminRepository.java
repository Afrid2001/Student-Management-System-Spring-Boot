package com.example.smsw.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.smsw.entity.Admin;

public interface AdminRepository
        extends JpaRepository<Admin, Long> {

    Admin findByUsernameAndPassword(
            String username,
            String password);
    Admin findByUsername(String username);
}