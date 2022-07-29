package com.xbs.util;

public class Constants {

	private Constants() {}

	public static final String IS_MANDATORY = "is a Mandatory field";

	public static final String PINCODE_REGEX = "^[1-9][\\d]{5}$";

	public static final String EMAIL_REGEX = "^[\\p{L}\\d\\+-]+(?:\\.[\\w\\+-]+)*@[\\p{L}\\d]+\\.([\\p{L}\\d]{2,4}\\.)?[\\p{L}]{2,4}$";

	public static final String INVALID_EMAIL = "Invalid Email Id";

}
