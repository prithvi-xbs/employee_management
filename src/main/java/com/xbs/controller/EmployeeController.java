package com.xbs.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xbs.request.dto.EmployeeRequestDto;
import com.xbs.response.dto.EmployeeResponseDto;
import com.xbs.service.EmployeeService;

@RestController
@RequestMapping("/v1/api/employee")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	@PostMapping
	public ResponseEntity<?> createEmployee(@Valid @RequestBody EmployeeRequestDto request) {
		EmployeeResponseDto response = employeeService.createEmployee(request);
		if (response == null)
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping
	public ResponseEntity<?> updateEmployee(@Valid @RequestBody EmployeeRequestDto request) {
		EmployeeResponseDto response = employeeService.updateEmployee(request);
		if (response == null)
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getEmployeeById(@PathVariable("id") String id) {
		EmployeeResponseDto response = employeeService.getEmployeeById(id);
		if (response == null)
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping()
	public ResponseEntity<?> getAllEmployeesList() {
		List<EmployeeResponseDto> response = employeeService.getAllEmployeesList();
		if (response.isEmpty())
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteEmployeeById(@PathVariable("id") String id) {
		Boolean response = employeeService.deleteEmployeeById(id);
		if (response == null || !response)
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		return ResponseEntity.status(HttpStatus.OK).body("Record deleted");
	}

}
