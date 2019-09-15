package com.lendico.loan.annuity.calculator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestAnnuityCalculator {

	@Autowired
	private AnnuityCalculator calcService;

	@Test
	public void test_getAmountForPeriod() {

		final Double d = calcService.getAmountForPeriod(0.05 / 12, 5000.00, 24);
		assertEquals(Double.valueOf(219.36), d);
	}

	@Test
	public void test_interestForPeriod() {

		final Double d = calcService.interestForPeriod(0.05, 30, 5000.00);
		assertEquals(Double.valueOf(20.83), d);

	}

}
