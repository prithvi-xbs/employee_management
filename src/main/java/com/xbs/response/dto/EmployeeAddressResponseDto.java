package com.xbs.response.dto;

import com.xbs.projection.EmployeeAddressProjection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeAddressResponseDto {

	private String id;

	private String addressType;

	private String addressLine;

	private String pincode;

	private String district;

	private String state;

	private boolean active;

	public EmployeeAddressResponseDto(EmployeeAddressProjection projection) {
		this.id = projection.getId();
		this.addressType = projection.getAddressType();
		this.addressLine = projection.getAddressLine();
		this.pincode = projection.getPincode();
		this.district = projection.getDistrict();
		this.state = projection.getState();
		this.active = projection.isActive();
	}

}
