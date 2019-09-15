package com.lendico.loan.annuity.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lendico.loan.annuity.request.AnnuityRequest;
import com.lendico.loan.annuity.scheduler.Installment;
import com.lendico.loan.annuity.scheduler.AnnuityPaymentScheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class AnnuityRestController {
	private static final Logger logger = LoggerFactory.getLogger(AnnuityRestController.class);

	@Autowired
	private AnnuityPaymentScheduler annuityScheduler;

	@PostMapping(path = "/generate-plan")
	public List<Installment> annuityDisimbursement(@Valid @RequestBody AnnuityRequest annuityRequest) {

		logger.info("Start Date {}, Duration {}, Nominal Rate {}, Loan Amount {}", annuityRequest.getStartDate(),
				annuityRequest.getDuration(), annuityRequest.getNominalRate(), annuityRequest.getLoanAmount());
		List<Installment> insts = annuityScheduler.createSchedule(annuityRequest.getStartDate(),
				annuityRequest.getDuration(), annuityRequest.getNominalRate(), annuityRequest.getLoanAmount());
		insts.stream().forEach(new Consumer<Installment>() {

			@Override
			public void accept(Installment i) {
				logger.info(i.toString());

			}

		});
		return insts;

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
