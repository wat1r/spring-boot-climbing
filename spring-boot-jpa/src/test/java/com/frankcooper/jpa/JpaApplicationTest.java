package com.frankcooper.jpa;

import com.frankcooper.jpa.entity.Student;
import com.frankcooper.jpa.manager.UserInfoManager;
import com.frankcooper.jpa.repository.StuRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JpaApplicationTest {

    @Autowired
    private StuRepository stuRepository;
    @Autowired
    private UserInfoManager userInfoManager;

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
//        userInfoManager.findByCondition()
    }
}
