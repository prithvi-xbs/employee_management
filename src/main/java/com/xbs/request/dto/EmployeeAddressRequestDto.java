package com.xbs.request.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.xbs.util.Constants;

import lombok.Data;

@Data
public class EmployeeAddressRequestDto {

	// only to be used while updating the record
	private String id;

	@NotNull(message = Constants.IS_MANDATORY)
	private String addressType;

	@NotBlank(message = Constants.IS_MANDATORY)
	private String addressLine;

	private String pincode;

	private String district;

	private String state;

	private boolean active;

}
