package com.xbs.repository;

import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.xbs.entity.Employees;
import com.xbs.response.dto.EmployeeResponseDto;

@Repository
public interface EmployeeRepository extends JpaRepository<Employees, String> {

	boolean existsByIdAndDeletedNot(String id, boolean deleted);
	
	boolean existsByEmailAndIdNot(String email, String id);

	@Query("SELECT new com.xbs.response.dto.EmployeeResponseDto(e.id, e.employeeName, e.email, e.active) "
			+ "FROM #{#entityName} e "
			+ "WHERE e.deleted=false")
	CopyOnWriteArrayList<EmployeeResponseDto> findAllEmployees();

	@Modifying
	@Query("UPDATE #{#entityName} SET deleted=true, active=false WHERE id=:id")
	void deleteEmployeeById(String id);

	@Query("SELECT deleted FROM #{#entityName} WHERE id=:id")
	boolean findDeletedByEmployeeId(String id);

	@Query("SELECT new com.xbs.response.dto.EmployeeResponseDto(e.id, e.employeeName, e.email, e.active) "
			+ "FROM #{#entityName} e "
			+ "WHERE e.deleted=false AND e.id=:id")
	EmployeeResponseDto findByEmployeeId(String id);

	boolean existsByIdAndDeletedFalse(String id);

}
