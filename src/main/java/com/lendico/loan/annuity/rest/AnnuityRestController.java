package com.lendico.loan.annuity.rest;

import java.util.List;
import java.util.function.Consumer;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.lendico.loan.annuity.request.AnnuityRequest;
import com.lendico.loan.annuity.scheduler.Installment;
import com.lendico.loan.annuity.scheduler.PaymentScheduler;

@RestController
public class AnnuityRestController {

	@Autowired
	private PaymentScheduler annuityScheduler;

	@PostMapping(path = "/generate-plan")
	@ResponseBody
	public List<Installment> annuityDisimbursement(@Valid @RequestBody AnnuityRequest annuityRequest) {

		System.out.print(annuityRequest.toString());
		List<Installment> insts = annuityScheduler.createScheduler(annuityRequest.getStartDate(), annuityRequest.getDuration(),
				annuityRequest.getNominalRate(), annuityRequest.getLoanAmount());
		insts.stream().forEach(new Consumer<Installment>() {

			@Override
			public void accept(Installment i) {
				//System.out.print(i.toString() + System.lineSeparator());
				
			}
			
		});
		return insts;

	}

}
