package com.xbs.request.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.xbs.util.Constants;

import lombok.Data;

@Data
public class EmployeeRequestDto {
	
	private String id;

	@NotBlank(message = Constants.IS_MANDATORY)
	private String name;

	@NotBlank(message = Constants.IS_MANDATORY)
	@Pattern(regexp = Constants.EMAIL_REGEX, message = Constants.INVALID_EMAIL)
	private String email;

	private List<EmployeeAddressRequestDto> address = new ArrayList<>();

	private boolean active;

}
