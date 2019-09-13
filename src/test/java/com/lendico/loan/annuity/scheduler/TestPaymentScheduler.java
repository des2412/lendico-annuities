package com.lendico.loan.annuity.scheduler;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestPaymentScheduler {

	@Autowired
	private PaymentScheduler schd;

	@Test
	public void testCreateSchedule() {

		String text = "2018-01-01T00:00:00Z";
		List<Installment> res = schd.createScheduler(text, 24, 5.00, 5000);
		assertEquals(24, res.size());
	}

}
