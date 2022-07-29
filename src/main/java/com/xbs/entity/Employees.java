package com.xbs.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.GenericGenerator;

import com.xbs.util.Constants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "employees")
@Getter
@Setter
@ToString
public class Employees {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(name = "id", columnDefinition = "CHAR(32)")
	private String id;

	@NotBlank(message = Constants.IS_MANDATORY)
	@Column(name = "employee_name")
	private String employeeName;

	@NotBlank(message = Constants.IS_MANDATORY)
	@Pattern(regexp = Constants.EMAIL_REGEX)
	@Column(name = "email", unique = true)
	private String email;

	@Column(name = "active", columnDefinition = "boolean default true")
	private boolean active;

	@Column(name = "deleted", columnDefinition = "boolean default false")
	private boolean deleted;

}
