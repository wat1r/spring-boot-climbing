package com.frankcooper.jpa.repository;

import com.frankcooper.jpa.entity.UserInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserRepository extends JpaRepository<UserInfoEntity, Long>, JpaSpecificationExecutor<UserInfoEntity> {

    List<UserInfoEntity> findByFirstNameAndLastName(String firstName, String lastName);
}

