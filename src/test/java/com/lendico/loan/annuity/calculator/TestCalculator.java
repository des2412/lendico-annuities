package com.lendico.loan.annuity.calculator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestCalculator {

	@Autowired
	private AnnuityCalculator calcService;

	@Test
	public void test() {

		Double d = calcService.getAmountForPeriod(0.05 / 12, 5000.00, 24);
		assertEquals(Double.valueOf(219.36), d);
	}

	@Test
	public void testInterestCalc() {
		double ann = calcService.getAmountForPeriod(0.05 / 12, 5000.00, 24);
		double principal = 5000;

		Double d = calcService.interestForPeriod(0.05, 30, principal);
		assertEquals(Double.valueOf(20.83), d);

	}

}
