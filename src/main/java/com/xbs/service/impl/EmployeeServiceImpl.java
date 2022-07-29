package com.xbs.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.transaction.Transactional;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import com.xbs.entity.Address;
import com.xbs.entity.Employees;
import com.xbs.enums.AddressType;
import com.xbs.projection.EmployeeAddressProjection;
import com.xbs.repository.AddressRepository;
import com.xbs.repository.EmployeeRepository;
import com.xbs.request.dto.EmployeeAddressRequestDto;
import com.xbs.request.dto.EmployeeRequestDto;
import com.xbs.response.dto.EmployeeAddressResponseDto;
import com.xbs.response.dto.EmployeeResponseDto;
import com.xbs.service.EmployeeService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private AddressRepository addressRepository;

	/**
	 * create an employee
	 * 
	 * @param request
	 */
	@Override
	@Transactional
	public EmployeeResponseDto createEmployee(EmployeeRequestDto request) {
		request.getAddress().forEach(address -> {
			if (!EnumUtils.isValidEnum(AddressType.class, AddressType.valueOf(address.getAddressType()).toString()))
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No such Address Type present");
		});
		try {
			EmployeeResponseDto response = new EmployeeResponseDto();
			List<EmployeeAddressResponseDto> addressList = new ArrayList<>();
			Employees employee = modelMapper.map(request, Employees.class);
			employee.setActive(request.isActive());
			employee = employeeRepository.save(employee);
			if (!StringUtils.isEmpty(employee.getId())) {
				request.setId(employee.getId());
				createOrUpdateEmployee(request, addressList, employee);
				if (!CollectionUtils.isEmpty(addressList)) {
					response = modelMapper.map(employee, EmployeeResponseDto.class);
					response.setAddress(addressList);
				}
			}
			return response;
		} catch (Exception e) {
			log.info(ExceptionUtils.getStackTrace(e));
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create Employee");
		}
	}

	/**
	 * Update an Employee using Id
	 * 
	 * @param request
	 */
	@Override
	@Transactional
	public EmployeeResponseDto updateEmployee(EmployeeRequestDto request) {
		String employeeId = request.getId();
		if (!employeeRepository.existsById(employeeId))
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such Employee Id found");

		if (employeeRepository.existsByEmailAndIdNot(request.getEmail(), employeeId))
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Record already exists with same details");

		request.getAddress().forEach(address -> {
			if (addressRepository.existsByEmployeeIdAndAddressTypeAndIdNot(employeeId,
					AddressType.valueOf(address.getAddressType()), address.getId()))
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Record already exists with same Address Type");
			if (!EnumUtils.isValidEnum(AddressType.class, AddressType.valueOf(address.getAddressType()).toString()))
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No such Address Type present");
		});
		try {
			EmployeeResponseDto response = new EmployeeResponseDto();
			List<EmployeeAddressResponseDto> addressList = new ArrayList<>();
			Employees employee = employeeRepository.getReferenceById(employeeId);
			modelMapper.map(request, Employees.class);
			employee.setActive(request.isActive());
			employee = employeeRepository.save(employee);
			if (!StringUtils.isEmpty(employee.getId())) {
				createOrUpdateEmployee(request, addressList, employee);
				if (!CollectionUtils.isEmpty(addressList)) {
					response = modelMapper.map(employee, EmployeeResponseDto.class);
					response.setAddress(addressList);
				}
			}
			return response;
		} catch (Exception e) {
			log.info(ExceptionUtils.getStackTrace(e));
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update Employee");
		}
	}

	/**
	 * Method used to create or update an employee
	 * 
	 * @param request
	 * @param addressList
	 * @param employee
	 */
	private void createOrUpdateEmployee(EmployeeRequestDto request, List<EmployeeAddressResponseDto> addressList,
			Employees employee) {
		for (EmployeeAddressRequestDto address : request.getAddress()) {
			if (address.getId() != null && !address.getId().isBlank()
					&& addressRepository.existsByEmployeeIdAndIdAndDeletedFalse(request.getId(), address.getId())) {
				Address empAddress = addressRepository.getReferenceById(address.getId());
				empAddress.setEmployee(employee);
				empAddress.setAddressType(AddressType.valueOf(address.getAddressType()));
				empAddress.setAddressLine(address.getAddressLine());
				empAddress.setPincode(address.getPincode());
				empAddress.setDistrict(address.getDistrict());
				empAddress.setState(address.getState());
				empAddress.setActive(address.isActive());
				empAddress = addressRepository.save(empAddress);
				if (!StringUtils.isBlank(empAddress.getId())) {
					EmployeeAddressResponseDto addressResponse = modelMapper.map(empAddress,
							EmployeeAddressResponseDto.class);
					addressList.add(addressResponse);
				}
			} else {
				Address empAddress = new Address();
				empAddress.setEmployee(employee);
				empAddress.setAddressType(AddressType.valueOf(address.getAddressType()));
				empAddress.setAddressLine(address.getAddressLine());
				empAddress.setPincode(address.getPincode());
				empAddress.setDistrict(address.getDistrict());
				empAddress.setState(address.getState());
				empAddress.setActive(address.isActive());
				empAddress = addressRepository.save(empAddress);
				if (!StringUtils.isBlank(empAddress.getId())) {
					EmployeeAddressResponseDto addressResponse = modelMapper.map(empAddress,
							EmployeeAddressResponseDto.class);
					addressList.add(addressResponse);
				}
			}
		}
	}

	/**
	 * Get employee by Id
	 * 
	 * @param id
	 */
	@Override
	public EmployeeResponseDto getEmployeeById(String id) {

		if (!employeeRepository.existsByIdAndDeletedNot(id, true))
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such Employee exist");

		try {
			EmployeeResponseDto response = employeeRepository.findByEmployeeId(id);
			List<EmployeeAddressProjection> addressProjection = addressRepository.getByEmployeeIdAndDeleted(id, false);
			List<EmployeeAddressResponseDto> addressResponseList = new ArrayList<>();
			if (!CollectionUtils.isEmpty(addressProjection)) {
				addressProjection
						.forEach(projection -> addressResponseList.add(new EmployeeAddressResponseDto(projection)));
				response.setAddress(addressResponseList);
			}
			return response;
		} catch (Exception e) {
			log.info(ExceptionUtils.getStackTrace(e));
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch Employee details");
		}
	}

	/**
	 * Get all the employees list without employee Details
	 */
	@Override
	public List<EmployeeResponseDto> getAllEmployeesList() {
		try {
			List<EmployeeResponseDto> response = new ArrayList<>();
			CopyOnWriteArrayList<EmployeeResponseDto> responseDto = employeeRepository.findAllEmployees();
			if (!CollectionUtils.isEmpty(responseDto)) {
				responseDto.forEach(empResponse -> {
					Employees employees = employeeRepository.getReferenceById(empResponse.getId());
					modelMapper.map(employees, EmployeeResponseDto.class);
					response.add(empResponse);
				});
			}
			return response;
		} catch (Exception e) {
			log.info(ExceptionUtils.getStackTrace(e));
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch all Employees detail");
		}
	}

	/**
	 * Mark Employee as deleted by id
	 * 
	 * @param id
	 */
	@Override
	@Transactional
	public Boolean deleteEmployeeById(String id) {
		if (!employeeRepository.existsById(id))
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such Employee Id found");

		try {
			if (addressRepository.existsByEmployeeIdAndDeletedFalse(id))
				addressRepository.deleteByEmployeeId(id);
			if (!addressRepository.existsByEmployeeIdAndDeletedFalse(id))
				employeeRepository.deleteEmployeeById(id);
			return employeeRepository.findDeletedByEmployeeId(id);
		} catch (Exception e) {
			log.info(ExceptionUtils.getStackTrace(e));
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete an Employee");
		}
	}

}
