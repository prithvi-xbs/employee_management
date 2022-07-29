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
import com.xbs.repository.EmployeeAddressProjection;
import com.xbs.repository.EmployeeAddressRepository;
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
	private EmployeeAddressRepository addressRepository;

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
//			employee.setActive(true);
//			employee.setDeleted(false);
			employee = employeeRepository.save(employee);
			if (!StringUtils.isEmpty(employee.getId())) {
				createOrUpdateEmployee(employee.getId(), request, addressList, employee);
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

	@Override
	@Transactional
	public EmployeeResponseDto updateEmployee(String id, EmployeeRequestDto request) {
		if (!employeeRepository.existsById(id))
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such Employee Id found");

		if (employeeRepository.existsByEmailAndIdNot(request.getEmail(), id))
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Record already exists with same details");

		request.getAddress().forEach(address -> {
			if (addressRepository.existsByEmployeeIdAndAddressType(id, AddressType.valueOf(address.getAddressType())))
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Record already exists with same Address Type");
			if (!EnumUtils.isValidEnum(AddressType.class, AddressType.valueOf(address.getAddressType()).toString()))
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No such Address Type present");
		});
		try {
			EmployeeResponseDto response = new EmployeeResponseDto();
			List<EmployeeAddressResponseDto> addressList = new ArrayList<>();
			Employees employee = employeeRepository.getReferenceById(id);
			modelMapper.map(request, Employees.class);
//			employee.setActive(true);
//			employee.setDeleted(false);
			employee = employeeRepository.save(employee);
			if (!StringUtils.isEmpty(employee.getId())) {
				createOrUpdateEmployee(id, request, addressList, employee);
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
	 * @param id
	 * @param request
	 * @param addressList
	 * @param employee
	 */
	private void createOrUpdateEmployee(String id, EmployeeRequestDto request,
			List<EmployeeAddressResponseDto> addressList, Employees employee) {
		for (EmployeeAddressRequestDto address : request.getAddress()) {
			if (addressRepository.existsByEmployeeIdAndAddressTypeAndDeletedFalse(id,
					AddressType.valueOf(address.getAddressType()))) {
				Address empAddress = addressRepository.getReferenceById(address.getId());
				empAddress.setEmployee(employee);
				empAddress.setAddressType(AddressType.valueOf(address.getAddressType()));
				empAddress.setAddressLine(address.getAddressLine());
				empAddress.setPincode(address.getPincode());
				empAddress.setDistrict(address.getDistrict());
				empAddress.setState(address.getState());
//				empAddress.setActive(true);
//				empAddress.setDeleted(false);
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
//				empAddress.setActive(true);
//				empAddress.setDeleted(false);
				empAddress = addressRepository.save(empAddress);
				if (!StringUtils.isBlank(empAddress.getId())) {
					EmployeeAddressResponseDto addressResponse = modelMapper.map(empAddress,
							EmployeeAddressResponseDto.class);
					addressList.add(addressResponse);
				}
			}
		}
	}

	@Override
	public EmployeeResponseDto getEmployeeById(String id) {

		if (!employeeRepository.existsByIdAndDeletedNot(id, true))
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such Employee exist");

		try {
			EmployeeResponseDto response = employeeRepository.findByEmployeeId(id);
			List<EmployeeAddressProjection> addressProjection = addressRepository.findAddressListByEmpId(id);
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

	@Override
	public List<EmployeeResponseDto> getAllEmployeesList() {
		try {
			List<EmployeeResponseDto> response = new ArrayList<>();
			CopyOnWriteArrayList<EmployeeResponseDto> responseDto = employeeRepository.findAllEmployees();
			if (!CollectionUtils.isEmpty(responseDto)) {
				responseDto.forEach(empResponse -> {
					List<EmployeeAddressProjection> addressProjection = addressRepository
							.findAddressListByEmpId(empResponse.getId());
					Employees employees = employeeRepository.getReferenceById(empResponse.getId());
					modelMapper.map(employees, EmployeeResponseDto.class);
					List<EmployeeAddressResponseDto> addressResponseList = new ArrayList<>();
					if (!CollectionUtils.isEmpty(addressProjection)) {
						addressProjection.forEach(
								projection -> addressResponseList.add(new EmployeeAddressResponseDto(projection)));
						empResponse.setAddress(addressResponseList);
					}
					response.add(empResponse);
				});
			}
			return response;
		} catch (Exception e) {
			log.info(ExceptionUtils.getStackTrace(e));
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch all Employees detail");
		}
	}

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
