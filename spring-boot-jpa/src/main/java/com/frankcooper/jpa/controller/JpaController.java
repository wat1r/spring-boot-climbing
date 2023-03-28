package com.frankcooper.jpa.controller;

import com.frankcooper.jpa.entity.Student;
import com.frankcooper.jpa.repository.StuRepository;
import com.frankcooper.jpa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jpa")
public class JpaController {

    @Autowired
    private StuRepository stuRepository;
    @Autowired
    private UserRepository userRepository;


    @GetMapping("/student/add")
    public boolean test() {
        Student student = new Student();
        student.setStuName("杜甫");
        student.setAge(32);
        stuRepository.save(student);
        return false;
    }

}
