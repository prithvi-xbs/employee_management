package com.xbs.enums;

public enum AddressType {

	PERMANENT("PERMANENT", "Permanent"), CURRENT("CURRENT", "Current"),
	CORRESPONDENCE("CORRESPONDENCE", "Correspondence"), OFFICE("OFFICE", "Office");

	private String code;

	private String value;

	public String getCode() {
		return code;
	}

	public String getValue() {
		return value;
	}

	AddressType(String code, String value) {
		this.code = code;
		this.value = value;
	}

}
