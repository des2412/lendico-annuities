package com.lendico.loan.annuity.rest;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.lendico.loan.annuity.calculator.AnnuityCalculator;
import com.lendico.loan.annuity.scheduler.AnnuityPaymentScheduler;
import com.lendico.loan.annuity.scheduler.Installment;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AnnuityRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AnnuityPaymentScheduler annuityScheduler;

	private static final MediaType JSON_UTF8 = new MediaType(APPLICATION_JSON_UTF8, Charset.forName("UTF8"));

	@MockBean
	private AnnuityCalculator annuityCalculator;

	private String VALID_ANNUITY_REQUEST = "{\"loanAmount\": 2000.00, \"nominalRate\": 5.0, \"duration\": 24, \"startDate\": \"2018-01-01T00:00:01Z\"}";

	private String INVALID_ANNUITY_REQUEST = "{\"nominalRate\": 5.0, \"duration\": 24, \"startDate\": \"2018-01-01T00:00:01Z\"}";

	@Test
	public void div_by_zero_500() throws Exception {
		when(annuityCalculator.getAmountForPeriod(anyDouble(), anyDouble(), anyInt()))
				.thenReturn(Double.POSITIVE_INFINITY);

		when(annuityScheduler.createSchedule(anyString(), anyInt(), anyDouble(), anyDouble()))
				.thenReturn(new ArrayList<Installment>());

		mockMvc.perform(post("/generate-plan").content(VALID_ANNUITY_REQUEST).contentType(JSON_UTF8))
				.andExpect(status().is5xxServerError()).andExpect(content().contentType(JSON_UTF8));

		verify(annuityScheduler).createSchedule(anyString(), anyInt(), anyDouble(), anyDouble());

		//verify(annuityCalculator).getAmountForPeriod(anyDouble(), anyDouble(), anyInt());

	}

	/*
	 * Expect HTTP 200. {
	 * \"loanAmount\": 2000.00, "nominalRate": "5.0", "duration": 24, "startDate":
	 * "2018-01-01T00:00:01Z" }
	 */
	@Test
	public void ok_loanamt_200() throws Exception {

		List<Installment> installments = new ArrayList<>();
		Installment installment = new Installment();
		installments.add(installment);
		when(annuityScheduler.createSchedule(anyString(), anyInt(), anyDouble(), anyDouble())).thenReturn(installments);

		mockMvc.perform(post("/generate-plan").content(VALID_ANNUITY_REQUEST).contentType(JSON_UTF8))
				.andExpect(status().is2xxSuccessful()).andExpect(content().contentType(JSON_UTF8));

		verify(annuityScheduler).createSchedule(anyString(), anyInt(), anyDouble(), anyDouble());

	}

	/*
	 * Expect HTTP 400. { "nominalRate": "5.0", "duration": 24, "startDate":
	 * "2018-01-01T00:00:01Z" }
	 */
	@Test
	public void null_loanamt_400() throws Exception {

		mockMvc.perform(post("/generate-plan").content(INVALID_ANNUITY_REQUEST).contentType(JSON_UTF8))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.loanAmount", Is.is("must be greater than or equal to 1000.00")))
				.andExpect(content().contentType(JSON_UTF8));

		verify(annuityScheduler, never()).createSchedule(anyString(), anyInt(), anyDouble(), anyDouble());

	}

}
