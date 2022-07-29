package com.xbs.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.GenericGenerator;

import com.xbs.enums.AddressType;
import com.xbs.util.Constants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "address")
@Getter
@Setter
@ToString
public class Address {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(name = "id", columnDefinition = "CHAR(32)")
	private String id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id")
	@NotNull(message = Constants.IS_MANDATORY)
	private Employees employee;

	@Column(name = "address_type")
	@Enumerated(EnumType.STRING)
	@NotNull(message = Constants.IS_MANDATORY)
	private AddressType addressType;

	@Column(name = "address_line")
	@NotBlank(message = Constants.IS_MANDATORY)
	private String addressLine;

	@Pattern(regexp = Constants.PINCODE_REGEX)
	@NotBlank(message = Constants.IS_MANDATORY)
	private String pincode;

	@NotBlank(message = Constants.IS_MANDATORY)
	private String district;

	@NotBlank(message = Constants.IS_MANDATORY)
	private String state;

	@Column(name = "active", columnDefinition = "boolean default true")
	private boolean active;

	@Column(name = "deleted", columnDefinition = "boolean default false")
	private boolean deleted;

}
