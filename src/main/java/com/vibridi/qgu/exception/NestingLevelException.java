package com.vibridi.qgu.exception;

public class NestingLevelException extends Exception {
	private static final long serialVersionUID = 1L;

	public NestingLevelException() {
		super();
	}

	public NestingLevelException(String message) {
		super(message);
	}

	public NestingLevelException(Throwable cause) {
		super(cause);
	}

	public NestingLevelException(String message, Throwable cause) {
		super(message, cause);
	}

	public NestingLevelException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
