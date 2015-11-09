package com.acpcoursera.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GcmResponse implements Serializable {

	@JsonProperty("multicast_id")
	private long multicastId;
	private int success;
	private int failure;
	@JsonProperty("canonical_ids")
	private int canonicalIds;
	private List<Map<String, String>> results;

	@JsonIgnore
	int code;

	public GcmResponse() {

	}

	public long getMulticastId() {
		return multicastId;
	}

	public void setMulticastId(long multicastId) {
		this.multicastId = multicastId;
	}

	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	public int getFailure() {
		return failure;
	}

	public void setFailure(int failure) {
		this.failure = failure;
	}

	public int getCanonicalIds() {
		return canonicalIds;
	}

	public void setCanonicalIds(int canonicalIds) {
		this.canonicalIds = canonicalIds;
	}

	public List<Map<String, String>> getResults() {
		return results;
	}

	public void setResults(List<Map<String, String>> results) {
		this.results = results;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "GcmResponse code=" + code + ", [multicastId=" + multicastId
				+ ", success=" + success + ", failure=" + failure
				+ ", canonicalIds=" + canonicalIds + ", results=" + results + "]";
	}

}
