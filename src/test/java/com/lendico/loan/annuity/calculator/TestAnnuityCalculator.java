package com.lendico.loan.annuity.calculator;

import static org.junit.Assert.assertEquals;

import java.text.DecimalFormat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.lendico.loan.annuity.exception.DivideByZeroException;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AnnuityCalculator.class)
@TestPropertySource(locations = "/test.properties")
public class TestAnnuityCalculator {

	@Value("${decimal.format}")
	private String decFormat;

	@Autowired
	private AnnuityCalculator annuityCalculator;

	@Test
	public void test_calculate_annuity_amount() {

		final Double d = annuityCalculator.calculateAnnuityAmount(0.05 / 12, 5000.00, 24);
		assertEquals(219.36, Double.parseDouble(new DecimalFormat(decFormat).format(d)), 0);
	}

	@Test(expected = DivideByZeroException.class)
	public void test_zero_duration() {

		annuityCalculator.calculateAnnuityAmount(5.00, 5000.00, 0);
	}

	@Test
	public void test_calculate_annuity_monthly_interest() {

		final Double d = annuityCalculator.calculateAnnuityMonthlyInterest(0.05, 30, 5000.00);
		assertEquals(20.83, Double.parseDouble(new DecimalFormat(decFormat).format(d)), 0);

	}

}
