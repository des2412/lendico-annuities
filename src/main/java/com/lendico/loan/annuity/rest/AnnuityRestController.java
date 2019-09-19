package com.lendico.loan.annuity.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lendico.loan.annuity.request.AnnuityRequest;
import com.lendico.loan.annuity.scheduler.AnnuityPaymentScheduler;
import com.lendico.loan.annuity.scheduler.Installment;

@RestController
public class AnnuityRestController {
	private static final Logger logger = LoggerFactory.getLogger(AnnuityRestController.class);

	@Autowired
	private AnnuityPaymentScheduler annuityScheduler;

	@PostMapping(path = "/generate-plan")
	public ResponseEntity<Object> annuityDisimbursement(@Valid @RequestBody AnnuityRequest annuityRequest) {

		logger.info("Start Date {}, Duration {}, Nominal Rate {}, Loan Amount {}", annuityRequest.getStartDate(),
				annuityRequest.getDuration(), annuityRequest.getNominalRate(), annuityRequest.getLoanAmount());
		List<Installment> installments = annuityScheduler.createSchedule(annuityRequest.getStartDate(),
				annuityRequest.getDuration(), annuityRequest.getNominalRate(), annuityRequest.getLoanAmount());
		if (installments.isEmpty())
			return new ResponseEntity<>(installments, HttpStatus.INTERNAL_SERVER_ERROR);

		return new ResponseEntity<>(installments, HttpStatus.OK);

	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
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
