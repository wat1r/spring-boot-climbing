package com.frankcooper.jpa.manager;

import com.alibaba.fastjson.JSONObject;
import com.frankcooper.jpa.entity.Student;
import com.frankcooper.jpa.entity.UserInfoEntity;
import com.frankcooper.jpa.repository.UserRepository;
import com.frankcooper.jpa.request.UserInfoRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserInfoManager {

    @Autowired
    private UserRepository userRepository;


    public void findAll() {
        List<UserInfoEntity> all = userRepository.findAll();
        log.info("thing:{}", JSONObject.toJSONString(all));
    }
//
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


    /**
     * 根据名称查询对应的用户 模糊查询
     *
     * @param lastName
     */
    public void findUsersByLike(String lastName) {
        Specification<UserInfoEntity> specification = new Specification<UserInfoEntity>() {

            /**
             * 封装了单个的查询条件
             * @param root 查询对象的属性的封装
             * @param criteriaQuery 封装了需要执行查询中的各个部分的信息： select   from  order  by 等信息
             * @param criteriaBuilder 查询条件的构造器  定义不同的查询条件的
             * @return
             */
            @Override
            public Predicate toPredicate(Root<UserInfoEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                //封装条件，相当于where lastName like '%J%'
                Predicate pre = criteriaBuilder.like(root.get("lastName"), "%" + lastName + "%");
                return pre;
            }
        };

        List<UserInfoEntity> users = userRepository.findAll(specification);
        log.info("result list:{}", JSONObject.toJSONString(users));

    }


    /**
     * 根据名称查询对应的用户
     *
     * @param firstName
     */
    public void findUsersByName(String firstName) {
        Specification<UserInfoEntity> specification = new Specification<UserInfoEntity>() {

            /**
             * 封装了单个的查询条件
             * @param root 查询对象的属性的封装
             * @param criteriaQuery 封装了需要执行查询中的各个部分的信息： select   from  order  by 等信息
             * @param criteriaBuilder 查询条件的构造器  定义不同的查询条件的
             * @return
             */
            @Override
            public Predicate toPredicate(Root<UserInfoEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                //封装条件，相当于where firstName ='Tom'
                Predicate pre = criteriaBuilder.equal(root.get("firstName"), firstName);
                return pre;
            }
        };

        List<UserInfoEntity> users = userRepository.findAll(specification);
        log.info("result list:{}", JSONObject.toJSONString(users));

    }

    /**
     * 多条件查询，使用and关联查询条件
     *
     * @param firstName
     * @param lastName
     */
    public void findUsersByConditions(String firstName, String lastName) {
        Specification<UserInfoEntity> spec = new Specification<UserInfoEntity>() {
            /***
             *  Predicate ：封装了单个的查询条件
             * @param root  查询对象的属性的封装
             * @param criteriaQuery 封装了需要执行查询中的各个部分的信息： select   from  order  by 等信息
             * @param criteriaBuilder  CriteriaBuilder:查询条件的构造器  定义不同的查询条件的
             * @return
             */
            @Override
            public Predicate toPredicate(Root<UserInfoEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                // where first_name ='Tom' and  last_name='Jackson'
                List<Predicate> list = new ArrayList<>();
                list.add(criteriaBuilder.equal(root.get("firstName"), firstName));
                list.add(criteriaBuilder.equal(root.get("lastName"), lastName));
                Predicate[] arr = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(arr));
            }
        };
        List<UserInfoEntity> list = userRepository.findAll(spec);
        log.info("result list:{}", JSONObject.toJSONString(list));
    }


    /**
     * 多个条件查询,使用or关联查询条件
     *
     * @param firstName
     * @param lastName
     * @param id
     */
    public void findUsersByConditions(String firstName, String lastName, Long id) {
        /*
         *  Specification<Users> 封装了一个查询的对象
         */
        Specification<UserInfoEntity> spec = new Specification<UserInfoEntity>() {
            /***
             *  Predicate ：封装了单个的查询条件
             * @param root  查询对象的属性的封装
             * @param query 封装了需要执行查询中的各个部分的信息： select   from  order  by 等信息
             * @param cb  CriteriaBuilder:查询条件的构造器  定义不同的查询条件的
             */
            @Override
            public Predicate toPredicate(Root<UserInfoEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // where （firstName ='Tom' and  lastName='Jackson'） or id=2
                return cb.or(cb.and(cb.equal(root.get("firstName"), firstName), cb.equal(root.get("lastName"), lastName)), cb.equal(root.get("id"), id));
            }
        };
        List<UserInfoEntity> list = userRepository.findAll(spec);

    }

}
