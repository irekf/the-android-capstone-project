package com.acpcoursera.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acpcoursera.model.CheckInData;

public interface CheckInDataRepository extends JpaRepository<CheckInData, Integer> {

    public CheckInData findByUsername(String username);

    public List<CheckInData> findAllByUsername(String username);

}
