package com.frankcooper.jpa.repository;

import com.frankcooper.jpa.entity.UserInfoEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

public interface UserRepository extends JpaSpecificationExecutor<UserInfoEntity> {

}

