package com.frankcooper.jpa.repository;

import com.frankcooper.jpa.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StuRepository extends JpaRepository<Student,Long>, JpaSpecificationExecutor<Student> {

}
