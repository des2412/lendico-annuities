package com.lendico.loan.annuity.calculator;

import static java.lang.Math.pow;

import java.text.DecimalFormat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Slf4j
public class CalculatorService {

	private static final Logger logger = LoggerFactory.getLogger(CalculatorService.class);
	
	@Value(value = "${year.days}")
	private int yrDays;

	public double getAmountForPeriod(double rate, double amount, int duration) {

		final double denom = pow(1 + rate, -duration);
		if(denom == 0) {
			throw new RuntimeException("Divide by zero detected.");
		}
		final double annuityPayment = (rate * amount) / (1 - (denom));
		logger.info("Periodic payment for annuity {}", annuityPayment);
		return Double.parseDouble(new DecimalFormat("##.##").format(annuityPayment));

	}

	public double interestForPeriod(double rate, int mthDays, double principal) {
		double interest = (rate * mthDays * principal) / yrDays;
		logger.info("Interest for annuity {}", interest);
		return Double.parseDouble(new DecimalFormat("##.##").format(interest));

	}

}
