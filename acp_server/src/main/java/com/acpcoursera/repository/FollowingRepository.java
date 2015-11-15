package com.acpcoursera.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acpcoursera.model.Following;

public interface FollowingRepository extends JpaRepository<Following, Integer> {

    public Following findByUsername(String username);

    public Following findByUsernameAndFollowingName(String username, String followingName);

    public List<Following> findAllByUsername(String username);

    public Integer deleteByUsernameAndFollowingName(String username, String followingName);

}
