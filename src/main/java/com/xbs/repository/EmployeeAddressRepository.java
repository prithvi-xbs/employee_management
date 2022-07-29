package com.xbs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.xbs.entity.Address;
import com.xbs.enums.AddressType;

@Repository
public interface EmployeeAddressRepository extends JpaRepository<Address, String> {

	boolean existsByEmployeeIdAndAddressType(String id, AddressType addressType);

	@Query("SELECT ea.id AS id, ea.addressType AS addressType, ea.addressLine AS addressLine, ea.pincode AS pincode, "
			+ "ea.district AS district, ea.state AS state, ea.active AS active "
			+ "FROM #{#entityName} ea "
			+ "WHERE ea.deleted=false AND ea.employee.id=:empId ")
	List<EmployeeAddressProjection> findAddressListByEmpId(String empId);

	boolean existsByAddressType(AddressType addressType);

	boolean existsByEmployeeIdAndDeletedFalse(String id);

	@Modifying
	@Query("UPDATE #{#entityName} SET deleted=true, active=false WHERE employee.id=:id")
	void deleteByEmployeeId(String id);

	boolean existsByEmployeeIdAndAddressTypeAndDeletedFalse(String id, AddressType addressType);

}
