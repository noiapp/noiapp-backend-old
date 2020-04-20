/*
 * Created by Ubique Innovation AG
 * https://www.ubique.ch
 * Copyright (c) 2020. All rights reserved.
 */

package org.dpppt.backend.sdk.model;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Exposee {
	@JsonIgnore
	private Integer id;

	@NotNull
	private String key;

	// TODO MAKE LOCAL DATE??
	@NotNull
	private String onset;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@JsonIgnore
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOnset() {
		return onset;
	}

	public void setOnset(String onset) {
		this.onset = onset;
	}
}
