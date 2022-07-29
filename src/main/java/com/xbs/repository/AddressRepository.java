package com.xbs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.xbs.entity.Address;
import com.xbs.enums.AddressType;
import com.xbs.projection.EmployeeAddressProjection;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {

	boolean existsByEmployeeIdAndAddressType(String id, AddressType addressType);

	List<EmployeeAddressProjection> getByEmployeeIdAndDeleted(String employeeId, boolean isDeleted);

	boolean existsByAddressType(AddressType addressType);

	boolean existsByEmployeeIdAndDeletedFalse(String id);

	@Modifying
	@Query("UPDATE #{#entityName} SET deleted=true, active=false WHERE employee.id=:id")
	void deleteByEmployeeId(String id);

	boolean existsByEmployeeIdAndAddressTypeAndDeletedFalse(String id, AddressType addressType);

	boolean existsByEmployeeIdAndIdAndDeletedFalse(String employeeId, String id);

	boolean existsByEmployeeIdAndAddressTypeAndIdNot(String employeeId, AddressType addressType, String id);

}
