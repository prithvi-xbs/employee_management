package com.xbs.service;

import java.util.List;

import com.xbs.request.dto.EmployeeRequestDto;
import com.xbs.response.dto.EmployeeResponseDto;

public interface EmployeeService {

	EmployeeResponseDto createEmployee(EmployeeRequestDto request);

	EmployeeResponseDto updateEmployee(String id, EmployeeRequestDto request);

	EmployeeResponseDto getEmployeeById(String id);

	List<EmployeeResponseDto> getAllEmployeesList();

	Boolean deleteEmployeeById(String id);

}
