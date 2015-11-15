package com.acpcoursera.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acpcoursera.model.CheckInData;
import com.acpcoursera.model.Follower;

public interface FollowerRepository extends JpaRepository<Follower, Integer> {

    public CheckInData findByUsername(String username);

    public List<Follower> findAllByUsername(String username);

}
