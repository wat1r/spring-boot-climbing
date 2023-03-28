package com.frankcooper.jpa.entity;

import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity(name = "UserInfoEntity")
@Table(name = "user_info", schema = "cooper_jpa_test")
public class UserInfoEntity implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "first_name", nullable = true, length = 100)
    private String firstName;
    @Column(name = "last_name", nullable = true, length = 100)
    private String lastName;
    @Column(name = "telephone", nullable = true, length = 100)
    private String telephone;
    @Column(name = "create_time", nullable = true)
    private Date createTime;
    @Column(name = "version", nullable = true)
    private String version;
    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id", name = "address_id", nullable = false)
    @Fetch(FetchMode.JOIN)
    private UserReceivingAddressEntity addressEntity;
}

