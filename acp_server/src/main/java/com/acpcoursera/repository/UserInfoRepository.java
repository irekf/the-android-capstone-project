package com.acpcoursera.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acpcoursera.model.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {

    public Collection<UserInfo> findByUsername(String username);

}
