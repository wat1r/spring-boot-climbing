package com.frankcooper.singlePoint;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Description: 用户实体
 * @Author: zhouhui
 * @Version: V1.0
 * @Date: 2019/4/28 9:05
 */
@Getter
@Setter
@ToString
public class User implements Serializable {
    private static final long serialVersionUID = -8421607336038680103L;
    // 姓名
    private String name;

    // 年龄
    private Integer age;

    // 性别
    private Boolean sex;

}
