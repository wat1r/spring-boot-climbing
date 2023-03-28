package com.frankcooper.jpa.entity;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stu_id")
    private Long stuId;
    @Column(name = "stu_name")
    private String stuName;
    @Column(name = "stu_age")
    private int age;

}

