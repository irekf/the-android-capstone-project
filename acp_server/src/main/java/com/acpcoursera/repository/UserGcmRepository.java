package com.acpcoursera.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acpcoursera.model.UserGcm;

public interface UserGcmRepository extends JpaRepository<UserGcm, Integer> {

    public UserGcm findByUsername(String username);

}
