/**
 * 
 */
package com.lendico.loan.annuity.rest;

import java.time.LocalDateTime;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.lendico.loan.annuity.exception.DivideByZeroException;
import com.lendico.loan.annuity.exception.ExceptionResponse;

/**
 * @author dg8wi
 *
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class AnnuityResponseExceptionHandler extends ResponseEntityExceptionHandler {

	@ResponseStatus(INTERNAL_SERVER_ERROR)
	@ExceptionHandler({ DivideByZeroException.class })
	public final ResponseEntity<Object> handleDivideByZeroException(DivideByZeroException exc, WebRequest req) {
		ExceptionResponse excResp = new ExceptionResponse(LocalDateTime.now(), INTERNAL_SERVER_ERROR.value(),
				req.getDescription(false));
		return new ResponseEntity<>(excResp, INTERNAL_SERVER_ERROR);

	}

}
