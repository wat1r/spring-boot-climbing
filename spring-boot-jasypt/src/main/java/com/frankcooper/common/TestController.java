package com.frankcooper.common;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
public class TestController {


    @Value("${spring.datasource.password}")
    private String password;

    @GetMapping("/jasypt/get")
    public JSONObject getJasypt() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("password", password);
        return jsonObject;
    }


}
