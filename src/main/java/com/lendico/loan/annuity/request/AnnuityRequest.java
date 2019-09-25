package com.lendico.loan.annuity.request;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.ToString;

@ToString(includeFieldNames = true)
@Data
public class AnnuityRequest {

	@Min(6)
	@Max(120)
	private int duration;
	@DecimalMax("12.00")
	@DecimalMin("2.00")
	private double nominalRate;
	@DecimalMax("100000.00")
	@DecimalMin("1000.00")
	private double loanAmount;
	@NotNull(message = "Please provide a start date")
	private String startDate;

}
