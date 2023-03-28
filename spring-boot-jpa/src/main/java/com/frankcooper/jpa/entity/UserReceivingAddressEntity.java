package com.frankcooper.jpa.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import java.io.Serializable;

@Data
@Entity(name = "UserReceivingAddressEntity")
@Table(name = "user_receiving_address", schema = "cooper_jpa_test")
public class UserReceivingAddressEntity  implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    @Column(name = "address_city", nullable = true, length = 500)
    private String addressCity;


}
