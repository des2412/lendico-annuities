package com.lendico.loan.annuity.exception;

/**
 * thrown if Double divided by zero.
 * @author dg8wi
 *
 */
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
