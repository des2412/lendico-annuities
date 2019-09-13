package com.lendico.loan.annuity.calculator;

import static java.lang.Math.pow;

import java.text.DecimalFormat;

import org.springframework.stereotype.Service;

@Service
public class CalculatorService {

	public double getAmountForPeriod(double rate, double amount, int duration) {

		final double raw = (rate * amount) / (1 - (pow(1 + rate, -duration)));
		
		return Double.parseDouble(new DecimalFormat("##.##").format(raw));

	}

	public double interestForPeriod(double rate, int mthDays, double principal) {
		// Interest = (Rate * Days in Month * Initial Outstanding Principal) / Days in
		// Year

		double raw = (rate * mthDays * principal) / 360;
		return Double.parseDouble(new DecimalFormat("##.##").format(raw));

	}

}
