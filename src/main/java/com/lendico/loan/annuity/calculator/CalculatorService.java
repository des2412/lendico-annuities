package com.lendico.loan.annuity.calculator;

import static java.lang.Math.pow;

import java.text.DecimalFormat;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Slf4j
public class CalculatorService {

	private static final Logger logger = LoggerFactory.getLogger(CalculatorService.class);

	public double getAmountForPeriod(double rate, double amount, int duration) {

		final double annuityPayment = (rate * amount) / (1 - (pow(1 + rate, -duration)));
		logger.info("CalculatorService: Periodic payment for annuity {}", annuityPayment);
		return Double.parseDouble(new DecimalFormat("##.##").format(annuityPayment));

	}

	public double interestForPeriod(double rate, int mthDays, double principal) {
		double interest = (rate * mthDays * principal) / 360;
		logger.info("CalculatorService: Interest for annuity {}", interest);
		return Double.parseDouble(new DecimalFormat("##.##").format(interest));

	}

}
