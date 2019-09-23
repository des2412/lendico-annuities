package com.lendico.loan.annuity.scheduler;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.lendico.loan.annuity.exception.DivideByZeroException;
import com.lendico.loan.annuity.model.Installment;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestAnnuityScheduler {

	@Autowired
	private AnnuityScheduler annuityScheduler;
	private String dateTime = "2018-01-01T00:00:00Z";

	@Test
	public void testCreateSchedule() {

		List<Installment> res = annuityScheduler.createSchedule(dateTime, 24, 5.00, 5000.00);
		assertEquals(24, res.size());
	}

	@Test(expected = DivideByZeroException.class)
	public void testCreateScheduleZeroDuration() {

		annuityScheduler.createSchedule(dateTime, 0, 5.00, 5000.00);
	}

	@Test(expected = NullPointerException.class)
	public void testCreateScheduleNullStart() {
		annuityScheduler.createSchedule(null, 24, 5.00, 5000.00).size();
	}

}
