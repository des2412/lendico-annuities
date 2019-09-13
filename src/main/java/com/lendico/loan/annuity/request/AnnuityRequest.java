package com.lendico.loan.annuity.request;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class AnnuityRequest {

	@NotNull(message = "Please provide a duration")
	@Min(6)
	@Max(120)
	private int duration;
	@NotNull(message = "Please provide a rate")
	private double nominalRate;
	@NotNull(message = "Please provide an amount")
	@DecimalMin("1000.00")
	private double loanAmount;
	@NotNull(message = "Please provide a start date")
	private String startDate;

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public double getNominalRate() {
		return nominalRate;
	}

	public void setNominalRate(double rate) {
		this.nominalRate = rate;
	}

	public double getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(double amount) {
		this.loanAmount = amount;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	@Override
	public String toString() {
		return "AnnuityRequest [duration=" + duration + ", nominalRate=" + nominalRate + ", loanAmount=" + loanAmount
				+ ", startDate=" + startDate + "]";
	}

}
