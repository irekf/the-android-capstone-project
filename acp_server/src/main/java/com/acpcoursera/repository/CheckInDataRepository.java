package com.acpcoursera.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acpcoursera.model.CheckInData;

public interface CheckInDataRepository extends JpaRepository<CheckInData, Integer> {

    public CheckInData findByUsername(String username);

}
