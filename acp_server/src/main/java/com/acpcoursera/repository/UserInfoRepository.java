package com.acpcoursera.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acpcoursera.model.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {

    public UserInfo findByUsername(String username);

    public List<UserInfo> findAllByUserType(String userType);

}
