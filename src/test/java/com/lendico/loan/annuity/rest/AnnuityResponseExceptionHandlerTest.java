package com.lendico.loan.annuity.rest;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.lendico.loan.annuity.calculator.AnnuityCalculator;

/**
 * This test causes a HTTP 500. In runtime system this might conceivably happen.
 * 
 * @author dg8wi
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AnnuityResponseExceptionHandlerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AnnuityCalculator annuityCalculator;

	private static final MediaType JSON_UTF8 = new MediaType(APPLICATION_JSON_UTF8, Charset.forName("UTF8"));
	private String VALID_ANNUITY_REQUEST = "{\"loanAmount\": 2000.00, \"nominalRate\": 5.0, \"duration\": 24, \"startDate\": \"2018-01-01T00:00:01Z\"}";

	@Test
	public void div_by_zero_returns_http500() throws Exception {
		when(annuityCalculator.getAmountForPeriod(anyDouble(), anyDouble(), anyInt()))
				.thenReturn(Double.POSITIVE_INFINITY);
		mockMvc.perform(post("/generate-plan").content(VALID_ANNUITY_REQUEST).contentType(JSON_UTF8))
				.andExpect(status().is5xxServerError()).andExpect(content().contentType(JSON_UTF8));

		verify(annuityCalculator).getAmountForPeriod(anyDouble(), anyDouble(), anyInt());

	}

}
