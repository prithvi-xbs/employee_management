package com.xbs.service;

import java.util.List;

import com.xbs.request.dto.EmployeeRequestDto;
import com.xbs.response.dto.EmployeeResponseDto;

public interface EmployeeService {

	/**
	 * Create an employee
	 * 
	 * @param request
	 * @return
	 */
	EmployeeResponseDto createEmployee(EmployeeRequestDto request);

	/**
	 * Update an employee
	 * 
	 * @param request
	 * @return
	 */
	EmployeeResponseDto updateEmployee(EmployeeRequestDto request);

	/**
	 * get employee by Id
	 * 
	 * @param id
	 * @return
	 */
	EmployeeResponseDto getEmployeeById(String id);

	/**
	 * get all employees list
	 * 
	 * @return
	 */
	List<EmployeeResponseDto> getAllEmployeesList();

	/**
	 * marked employee as deleted
	 * 
	 * @param id
	 * @return
	 */
	Boolean deleteEmployeeById(String id);

}
