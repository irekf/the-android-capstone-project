package com.acpcoursera.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class UserGcm {

    @Id
    @GeneratedValue
    private Integer id;

    private String username;

    private String token;

    public UserGcm() {
    }

    public UserGcm(String username, String token) {
    	this.username = username;
    	this.token = token;
    }

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}

}
