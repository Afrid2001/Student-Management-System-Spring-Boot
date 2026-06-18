package com.example.smsw.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.smsw.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrCollegeNameContainingIgnoreCase(
            String firstName,
            String lastName,
            String collegeName);
}
