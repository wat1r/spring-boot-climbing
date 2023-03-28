package com.frankcooper.jpa.manager;

import com.frankcooper.jpa.entity.Student;
import com.frankcooper.jpa.repository.StuRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class StuManager {

    @Autowired
    private StuRepository stuRepository;

    public void findAll() {
        List<Student> all = stuRepository.findAll();
        log.info("size:{}",all.size());
    }
}
