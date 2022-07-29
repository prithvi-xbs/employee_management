package com.xbs.repository;

public interface EmployeeAddressProjection {

	public String getId();

	public String getAddressType();

	public String getAddressLine();

	public String getPincode();

	public String getDistrict();

	public String getState();

	public boolean isActive();

}
