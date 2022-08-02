package com.frankcooper.mdc;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;

/**
 * @author: wangzhou(Frank Cooper)
 * @date: 2022/7/28 16:50
 * @description:
 */
@Slf4j
public class SimpleMDC {

    private static final Logger logger = LoggerFactory.getLogger(SimpleMDC.class);
    public static final String REQ_ID = "REQ_ID";


    public static void main(String[] args) {
        test2();
    }


    private static void test1() {
        log.info("test1=======");
        for (int i = 0; i < 1; i++) {
            MDC.put(REQ_ID, UUID.randomUUID().toString());
            logger.info("开始调用服务A，进行业务处理");
            logger.info("业务处理完毕，可以释放空间了，避免内存泄露");
            MDC.remove(REQ_ID);
            logger.info("REQ_ID 还有吗？{}", MDC.get(REQ_ID) != null);
        }
    }


    private static void test2() {
        log.info("test2=======");
        for (int i = 0; i < 1; i++) {
            MDC.put(REQ_ID, JSON.toJSONString(new Person(18, "Alice")));
            logger.info("开始调用服务A，进行业务处理");
            logger.info("业务处理完毕，可以释放空间了，避免内存泄露");

            String json = MDC.get(REQ_ID);
            Person person = JSON.parseObject(json, Person.class);


            MDC.remove(REQ_ID);
            logger.info("REQ_ID 还有吗？{}", MDC.get(REQ_ID) != null);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Person {
        private Integer age;
        private String name;
    }


}
