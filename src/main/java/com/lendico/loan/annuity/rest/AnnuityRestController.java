package com.lendico.loan.annuity.rest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lendico.loan.annuity.request.AnnuityRequest;
import com.lendico.loan.annuity.scheduler.AnnuityScheduler;

@RestController
public class AnnuityRestController {
	private static final Logger logger = LoggerFactory.getLogger(AnnuityRestController.class);

	private AnnuityScheduler annuityScheduler;

	@Autowired
	public AnnuityRestController(AnnuityScheduler annuityScheduler) {

		this.annuityScheduler = annuityScheduler;
	}

	@PostMapping(path = "/generate-plan")
	public ResponseEntity<Object> annuityDisimbursement(@Valid @RequestBody final AnnuityRequest annuityRequest) {

		final String startDate = annuityRequest.getStartDate();
		final int duration = annuityRequest.getDuration();
		final double nominalRate = annuityRequest.getNominalRate();
		final double loanAmt = annuityRequest.getLoanAmount();

		logger.info("Start Date {}, Duration {}, Nominal Rate {}, Loan Amount {}", startDate, duration, nominalRate,
				loanAmt);

		return new ResponseEntity<>(annuityScheduler.createSchedule(startDate, duration, nominalRate, loanAmt), OK);

	}

	/**
	 * Validates input arguments.
	 * 
	 * @param ex the MethodArgumentNotValidException.
	 * @return the Map of field name to error.
	 */
	@ResponseStatus(BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return errors;
	}

}
