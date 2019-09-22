package com.lendico.loan.annuity.scheduler;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.lendico.loan.annuity.model.Installment;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestAnnuityScheduler {

	@Autowired
	private AnnuityScheduler schd;

	@Test
	public void testCreateSchedule() {

		String dateTime = "2018-01-01T00:00:00Z";
		List<Installment> res = schd.createSchedule(dateTime, 24, 5.00, 5000.00);
		assertEquals(24, res.size());
	}

	@Test(expected = NullPointerException.class)
	public void testCreateScheduleNullStart() {
		schd.createSchedule(null, 24, 5.00, 5000.00).size();
	}

}
