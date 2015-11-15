package com.acpcoursera.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acpcoursera.model.Follower;

public interface FollowerRepository extends JpaRepository<Follower, Integer> {

    public Follower findByUsername(String username);

    public Follower findByUsernameAndFollowerName(String username, String followerName);

    public List<Follower> findAllByUsername(String username);

    public Integer deleteByUsernameAndFollowerName(String username, String followerName);

}
