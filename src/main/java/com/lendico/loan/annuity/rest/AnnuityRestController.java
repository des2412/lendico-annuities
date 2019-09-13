package com.lendico.loan.annuity.rest;

import java.util.List;
import java.util.function.Consumer;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.lendico.loan.annuity.request.AnnuityRequest;
import com.lendico.loan.annuity.scheduler.Installment;
import com.lendico.loan.annuity.scheduler.PaymentScheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class AnnuityRestController {
	private static final Logger logger = LoggerFactory.getLogger(AnnuityRestController.class);

	@Autowired
	private PaymentScheduler annuityScheduler;

	@PostMapping(path = "/generate-plan")
	public List<Installment> annuityDisimbursement(@Valid @RequestBody AnnuityRequest annuityRequest) {

		logger.info("Start Date {}, Duration {}, Nominal Rate {}, Loan Amount {}", annuityRequest.getStartDate(),
				annuityRequest.getDuration(), annuityRequest.getNominalRate(), annuityRequest.getLoanAmount());
		List<Installment> insts = annuityScheduler.createScheduler(annuityRequest.getStartDate(),
				annuityRequest.getDuration(), annuityRequest.getNominalRate(), annuityRequest.getLoanAmount());
		insts.stream().forEach(new Consumer<Installment>() {

			@Override
			public void accept(Installment i) {
				// System.out.print(i.toString() + System.lineSeparator());

			}

		});
		return insts;

	}

}
