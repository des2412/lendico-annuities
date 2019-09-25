package com.lendico.loan.annuity.scheduler;

import static org.junit.Assert.assertEquals;

import java.time.format.DateTimeFormatter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.lendico.loan.annuity.exception.DivideByZeroException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestAnnuityScheduler {

	private final int duration = 24;

	@Autowired
	private AnnuityScheduler annuityScheduler;
	private final String dateTime = "2018-01-01T00:00:00Z";

	@Test
	public void testCreateScheduleExpectInstallmentsEqDuration() {

		assertEquals(duration, annuityScheduler.createSchedule(dateTime, duration, 5.00, 5000.00).size());
	}

	@Test
	public void testCreateScheduleExpectDateMatch() {

		assertEquals(dateTime, annuityScheduler.createSchedule(dateTime, duration, 5.00, 5000.00).get(0).getDate()
				.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
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
