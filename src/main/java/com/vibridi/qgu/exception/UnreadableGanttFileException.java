package com.vibridi.qgu.exception;

public class UnreadableGanttFileException extends Exception {
	private static final long serialVersionUID = 1L;

	public UnreadableGanttFileException() {
		super();
	}

	public UnreadableGanttFileException(String message) {
		super(message);
	}

	public UnreadableGanttFileException(Throwable cause) {
		super(cause);
	}

	public UnreadableGanttFileException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnreadableGanttFileException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
