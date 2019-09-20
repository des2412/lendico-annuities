/**
 * 
 */
package com.lendico.loan.annuity.exception;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author dg8wi
 *
 */
public class ExceptionResponse {
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
	private LocalDateTime timestamp;
	private String message;
	private int status;
	private String error;

	public ExceptionResponse(LocalDateTime timestamp, String message, int status, String error) {
		this.timestamp = timestamp;
		this.message = message;
		this.status = status;
		this.error = error;
	}

	/**
	 * @return the timestamp
	 */
	public final LocalDateTime getTimestamp() {
		return timestamp;
	}

	/**
	 * @return the message
	 */
	public final String getMessage() {
		return message;
	}

	/**
	 * @return the status
	 */
	public final int getStatus() {
		return status;
	}

	/**
	 * @return the error
	 */
	public final String getError() {
		return error;
	}

}
