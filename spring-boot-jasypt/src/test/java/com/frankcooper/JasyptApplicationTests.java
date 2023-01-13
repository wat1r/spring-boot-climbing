package com.frankcooper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JasyptApplicationTests {


    @Value("${spring.datasource.password}")
    private String password;

    @Test
    void contextLoads() {
        System.out.println(password);
    }

}
