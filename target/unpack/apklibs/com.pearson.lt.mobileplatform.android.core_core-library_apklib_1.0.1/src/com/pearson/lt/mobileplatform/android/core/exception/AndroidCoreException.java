package com.pearson.lt.mobileplatform.android.core.exception;

public class AndroidCoreException extends RuntimeException {

	private static final long serialVersionUID = -5641855388301718926L;

	/**
	 * Wrapper exception class that all core exceptions will inherit from. 
	 * @param message The detail message for this exception.
	 */
	public AndroidCoreException(String message) {
		super(message);
	}

	/**
	 * Wrapper exception class that all core exceptions will inherit from. 
	 * @param message The detail message for this exception.
	 * @param cause The cause of this exception.
	 */
	public AndroidCoreException(String message, Throwable cause) {
		super(message, cause);
	}

}
