package com.sebatmedikal.remote.configuration;

public class ErrorCodes {

	public static final int USERNAME_OR_PASSWORD_INVALID = 0;
	public static final int USER_NOT_FOUND = 1;
	public static final int USERS_NOT_FOUND = 2;
	public static final int USER_IS_EXIST = 3;
	public static final int CREATED_USER_NOT_FOUND = 4;

	public static final int BRAND_NOT_FOUND = 5;
	public static final int BRANDS_NOT_FOUND = 6;
	public static final int BRAND_IS_EXIST = 7;

	public static final int PRODUCT_NOT_FOUND = 8;
	public static final int PRODUCTS_NOT_FOUND = 9;
	public static final int PRODUCT_IS_EXIST = 10;

	public static final int OPERATION_NOT_FOUND = 11;
	public static final int OPERATIONS_NOT_FOUND = 12;

	public static final int OPERATIONTYPE_NOT_FOUND = 13;
	public static final int OPERATIONTYPES_NOT_FOUND = 14;

	public static final int ROLE_NOT_FOUND = 15;
	public static final int ROLES_NOT_FOUND = 16;

	public static final int OPERATION_COUNT_0 = 17;
	public static final int INSUFFICIENT_STOCK = 18;

	public static final int NULL_PARAMETER01 = 19;
	public static final int UNKNOWN_OPERATION = 20;
	public static final int NULL_CONTROLLER = 21;
	public static final int UNKNOWN_CONTROLLER = 22;
	public static final int BAD_REQUEST = 23;

	public static final int UNKNOWN_ERROR = 24;

	public static final int USER_ALREADY_LOGOUT = 25;
	public static final int UNKNOWN_EXCEPTION = 26;
	public static final int LOGOUT_REQ_ANOTHER_USER = 27;
	public static final int GET_METHOD_NOT_SUPPORTED = 28;

	public static String getErrorMessage(int errorCode) {
		switch (errorCode) {
		case USERNAME_OR_PASSWORD_INVALID:
			return "Invalid username or password";
		case USER_NOT_FOUND:
			return "User not found";
		case USERS_NOT_FOUND:
			return "Users not found";
		case USER_IS_EXIST:
			return "User is already exist";
		case CREATED_USER_NOT_FOUND:
			return "Created user not found";
		case BRAND_NOT_FOUND:
			return "Brand not found";
		case BRANDS_NOT_FOUND:
			return "Brands not found";
		case BRAND_IS_EXIST:
			return "Brand is already exist";
		case PRODUCT_NOT_FOUND:
			return "Product not found";
		case PRODUCTS_NOT_FOUND:
			return "Products not found";
		case PRODUCT_IS_EXIST:
			return "Product already exist";
		case OPERATION_NOT_FOUND:
			return "Operation not found";
		case OPERATIONS_NOT_FOUND:
			return "Operations not found";
		case OPERATIONTYPE_NOT_FOUND:
			return "Operation type not found";
		case OPERATIONTYPES_NOT_FOUND:
			return "Operation types not found";
		case ROLE_NOT_FOUND:
			return "Role not found";
		case ROLES_NOT_FOUND:
			return "Roles not found";
		case OPERATION_COUNT_0:
			return "Operation count can not be 0";
		case INSUFFICIENT_STOCK:
			return "Insufficient product for operation";
		case NULL_PARAMETER01:// TODO:
			return "Parameter01 can not be null";
		case UNKNOWN_OPERATION:
			return "Unknown operation";
		case NULL_CONTROLLER:
			return "Controller can not be null";
		case UNKNOWN_CONTROLLER:
			return "Controller not found";
		case BAD_REQUEST:
			return "Bad request";
		case UNKNOWN_ERROR:
			return "Unknown error";
		case USER_ALREADY_LOGOUT:
			return "User already logout";
		case UNKNOWN_EXCEPTION:
			return "Unknown exception";
		case LOGOUT_REQ_ANOTHER_USER:
			return "User requested another user logout";
		case GET_METHOD_NOT_SUPPORTED:
			return "Get method not supported";
		default:
			return "Unknown response code: " + errorCode;
		}
	}
}
