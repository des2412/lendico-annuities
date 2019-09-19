package com.lendico.loan.annuity.exception;

public class DivideByZeroException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -5568611167507054800L;

	public DivideByZeroException() {
	}

	public DivideByZeroException(String message) {
		super(message);

	}

}
