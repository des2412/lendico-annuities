package com.lendico.loan.annuity.calculator;

import static java.lang.Math.pow;

import java.text.DecimalFormat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.lendico.loan.annuity.exception.DivideByZeroException;

@Component
public class AnnuityCalculator {

	@Value(value = "${year.days}")
	private int yrDays;

	@Value(value = "${decimal.format}")
	private String decFormat;

	/**
	 * 
	 * @param rate     the annuity interest rate.
	 * @param amount   the loan amount.
	 * @param duration the number of months.
	 * @return the cost (Interest + Principal) of annuity.
	 */
	public double calculateAnnuityAmount(double rate, double amount, int duration) {

		final double denom = pow(1 + rate, -duration);
		if (denom == 0 || 1 - denom == 0)
			throw new DivideByZeroException("Divide by zero detected.");

		return Double.parseDouble(new DecimalFormat(decFormat).format((rate * amount) / (1 - (denom))));

	}

	/**
	 * 
	 * @param rate      the annuity nominal rate.
	 * @param mthDays   the number of days in month, i.e., 30.
	 * @param principal the principal amount.
	 * @return the interest amount.
	 */
	public double calculateAnnuityMonthlyInterest(double rate, int mthDays, double principal) {

		return Double.parseDouble(new DecimalFormat(decFormat).format((rate * mthDays * principal) / yrDays));

	}

}
