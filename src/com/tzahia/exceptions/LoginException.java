package com.tzahia.exceptions;

public class LoginException extends Exception {

	private static final long serialVersionUID = 1L;

	public LoginException() {
		super("The user name or password you entered is not valid");
	}

}
