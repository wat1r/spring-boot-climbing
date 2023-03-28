package com.frankcooper.jpa.manager;

import com.frankcooper.jpa.entity.UserInfoEntity;
import com.frankcooper.jpa.repository.UserRepository;
import com.frankcooper.jpa.request.UserInfoRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserInfoManager {

    @Autowired
    private UserRepository userRepository;

    public Page<UserInfoEntity> findByCondition(UserInfoRequest userParam, Pageable pageable) {
        return userRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();
            if (StringUtils.isNoneBlank(userParam.getFirstName())) {
                //liked的查询条件
                predicates.add(cb.like(root.get("firstName"), "%" + userParam.getFirstName() + "%"));
            }
            if (StringUtils.isNoneBlank(userParam.getTelephone())) {
                //equal查询条件
                predicates.add(cb.equal(root.get("telephone"), userParam.getTelephone()));
            }
            if (StringUtils.isNoneBlank(userParam.getVersion())) {
                //greaterThan大于等于查询条件
                predicates.add(cb.greaterThan(root.get("version"), userParam.getVersion()));
            }
            if (userParam.getBeginCreateTime() != null && userParam.getEndCreateTime() != null) {
                //根据时间区间去查询   predicates.add(cb.between(root.get("createTime"),userParam.getBeginCreateTime(),userParam.getEndCreateTime()));
            }
            if (StringUtils.isNotBlank(userParam.getAddressCity())) {
                //联表查询，利用root的join方法，根据关联关系表里面的字段进行查询。
                predicates.add(cb.equal(root.join("addressEntityList").get("addressCity"), userParam.getAddressCity()));
            }
            return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
        }, pageable);
    }
}
