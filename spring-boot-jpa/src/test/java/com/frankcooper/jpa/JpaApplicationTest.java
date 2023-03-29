package com.frankcooper.jpa;

import com.alibaba.fastjson.JSONObject;
import com.frankcooper.jpa.entity.Student;
import com.frankcooper.jpa.entity.UserInfoEntity;
import com.frankcooper.jpa.manager.StuManager;
import com.frankcooper.jpa.manager.UserInfoManager;
import com.frankcooper.jpa.repository.StuRepository;
import com.frankcooper.jpa.repository.UserRepository;
import com.frankcooper.jpa.request.UserInfoRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class JpaApplicationTest {

    @Autowired
    private StuRepository stuRepository;
    @Autowired
    private StuManager stuManager;
    @Autowired
    private UserInfoManager userInfoManager;
    @Autowired
    private UserRepository userRepository;

    /**
     * 增
     * 创建一个实体类student添加到数据库,成功创建了
     */
    @Test
    public void addStudent() {
        Student student = new Student();
        student.setStuName("李白");
        student.setAge(22);
        stuRepository.save(student);
    }

    @Test
    public void queryUserInfo() {
        UserInfoRequest userParam = new UserInfoRequest();
        userParam.setFirstName("Tom");
        stuManager.findAll();
        userInfoManager.findAll();
//        userInfoManager.findByCondition(userParam, null);
    }


    @Test
    public void queryUserInfoOne() {
        userInfoManager.findUsersByConditions("Tom", "Jackson");
        userInfoManager.findUsersByConditions("Tom", "Jackson");
    }

    @Test
    public void queryUserInfoTwo() {
        userInfoManager.findUsersByLike( "Jack");
    }
    @Test
    public void findByFirstNameAndLastName() {
        List<UserInfoEntity> list = userRepository.findByFirstNameAndLastName("LeBorn", "James");
        log.info("result:{}",JSONObject.toJSONString(list));
    }




    @Test
    public void addUserInfoList() {
        String input = "[\n" +
                "\t{\n" +
                "\t\t\"addressEntity\": {\n" +
                "\t\t\t\"addressCity\": \"上海市\",\n" +
                "\t\t\t\"userId\": 1\n" +
                "\t\t},\n" +
                "\t\t\"createTime\": 1679972035000,\n" +
                "\t\t\"firstName\": \"Michel\",\n" +
                "\t\t\"lastName\": \"Jordan\",\n" +
                "\t\t\"telephone\": \"13101110222\",\n" +
                "\t\t\"version\": \"1\",\n" +
                "\t\t\"addressId\": 1\n" +
                "\t}\n" +
                " , " +
                "\t{\n" +
                "\t\t\"addressEntity\": {\n" +
                "\t\t\t\"addressCity\": \"重庆市\",\n" +
                "\t\t\t\"userId\": 1\n" +
                "\t\t},\n" +
                "\t\t\"createTime\": 1679972035000,\n" +
                "\t\t\"firstName\": \"LeBorn\",\n" +
                "\t\t\"lastName\": \"James\",\n" +
                "\t\t\"telephone\": \"13101110222\",\n" +
                "\t\t\"version\": \"1\",\n" +
                "\t\t\"addressId\": 1\n" +
                "\t}\n" +
                "]\n";
        List<UserInfoEntity> list = JSONObject.parseArray(input, UserInfoEntity.class);
        userRepository.saveAll(list);


    }
}
