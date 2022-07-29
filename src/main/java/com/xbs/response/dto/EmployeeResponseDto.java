package com.xbs.response.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDto {

	private String id;

	private String name;

	private String email;

	private List<EmployeeAddressResponseDto> address = new ArrayList<>();

	private boolean active;

	public EmployeeResponseDto(String id, String name, String email, boolean active) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.active = active;
	}

}
