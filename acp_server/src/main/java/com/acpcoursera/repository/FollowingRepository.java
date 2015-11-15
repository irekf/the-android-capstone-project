package com.acpcoursera.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acpcoursera.model.CheckInData;
import com.acpcoursera.model.Following;

public interface FollowingRepository extends JpaRepository<Following, Integer> {

    public CheckInData findByUsername(String username);

    public List<Following> findAllByUsername(String username);

}
