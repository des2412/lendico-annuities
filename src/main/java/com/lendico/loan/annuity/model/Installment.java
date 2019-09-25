package com.lendico.loan.annuity.model;

import java.time.ZonedDateTime;

import lombok.Data;
import lombok.ToString;

@ToString(includeFieldNames = true)
@Data
public class Installment {

	private double borrowerPaymentAmount;
	private ZonedDateTime date;
	private double initialOutstandingPrincipal;
	private double interest;
	private double principal;
	private double remainingOutstandingPrincipal;

}
