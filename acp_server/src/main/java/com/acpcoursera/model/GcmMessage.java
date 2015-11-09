package com.acpcoursera.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GcmMessage implements Serializable {

	@JsonProperty("registration_ids")
	private List<String> registrationIds;
	private Map<String, String> data;

	public GcmMessage() {
		registrationIds = new ArrayList<>();
		data = new HashMap<>();
	}

	public void addRecipient(String registrationToken) {
		registrationIds.add(registrationToken);
	}

	public void addDataField(String field, String value) {
		data.put(field, value);
	}

	public List<String> getRegistrationIds() {
		return registrationIds;
	}

	public void setRegistrationIds(List<String> registrationIds) {
		this.registrationIds = registrationIds;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "GcmMessage [registrationIds=" + registrationIds + ", data=" + data + "]";
	}

}
