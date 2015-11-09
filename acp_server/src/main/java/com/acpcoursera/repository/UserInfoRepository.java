package com.acpcoursera.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acpcoursera.model.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {

    public UserInfo findByUsername(String username);

}
