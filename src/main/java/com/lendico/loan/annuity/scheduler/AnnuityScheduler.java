package com.lendico.loan.annuity.scheduler;

import static java.time.LocalDateTime.parse;
import static java.time.ZoneOffset.of;
import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;

import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.lendico.loan.annuity.calculator.AnnuityCalculator;
import com.lendico.loan.annuity.exception.DivideByZeroException;
import com.lendico.loan.annuity.model.Installment;

@Service
public class AnnuityScheduler {
	private static final Logger logger = LoggerFactory.getLogger(AnnuityScheduler.class);

	@Value(value = "${year.months}")
	private int yrMonths;

	@Value(value = "${month.days}")
	private int monthDays;

	@Value(value = "${decimal.format}")
	private String decFormat;

	@Value(value = "${time.zone.offset}")
	private String timeZoneOffset;

	private AnnuityCalculator annuityCalculator;

	private DecimalFormat decimalFormat;

	private ZoneOffset zoneOffset;

	private List<Double> previousRemainingPrincipal;

	@Autowired
	public AnnuityScheduler(AnnuityCalculator annuityCalculator) {
		this.annuityCalculator = annuityCalculator;

	}

	@PostConstruct
	private void postConstruct() {
		this.decimalFormat = new DecimalFormat(decFormat);
		this.zoneOffset = of(timeZoneOffset);
		// holds one and only one value: the previous outstanding principal remaining.
		this.previousRemainingPrincipal = asList(new Double[1]);
	}

	/**
	 * 
	 * @param start    the start date time.
	 * @param duration the duration.
	 * @param rate     the interest rate.
	 * @param amount   the loan amount.
	 * @return list of Installments.
	 */
	public List<Installment> createSchedule(final String start, final Integer duration, final Double rate,
			final Double amount) {
		// perform basic null checks.
		requireNonNull(start, "Start date is required");
		requireNonNull(duration, "Duration is required");
		requireNonNull(rate, "Rate is required");
		requireNonNull(amount, "Amount is required");

		final double ratePercent = rate / 100;

		final double annuity = annuityCalculator.calculateAnnuityAmount(ratePercent / yrMonths, amount, duration);

		// can occur at runtime due to either unpredictable reason or duration of 0.
		if (Double.isInfinite(annuity)) {
			logger.error("Divide by zero detected");
			throw new DivideByZeroException();
		}

		// validate start.
		try {
			parse(start, ISO_ZONED_DATE_TIME);
		} catch (DateTimeParseException e) {
			logger.error(e.getMessage());
			throw e;
		}
		final OffsetDateTime dateTime = parse(start, ISO_ZONED_DATE_TIME).plusSeconds(0).atOffset(zoneOffset);
		// create Installments from start to duration times on monthly basis.
		return rangeClosed(0, duration - 1).mapToObj(n -> {
			final Installment installment = new Installment();
			if (n == 0) {
				installment.setDate(dateTime.toZonedDateTime());
				installment.setInitialOutstandingPrincipal(Double.parseDouble(decimalFormat.format(amount)));
			} else {
				installment.setDate(dateTime.plus(n, MONTHS).toZonedDateTime());
				installment.setInitialOutstandingPrincipal(previousRemainingPrincipal.get(0));
			}

			previousRemainingPrincipal.set(0,
					createInstallment(installment, ratePercent, annuity).getRemainingOutstandingPrincipal());

			return installment;
		}).collect(toList());

	}

	/**
	 * 
	 * @param installment the Installment.
	 * @param ratePercent the monthly rate as percent.
	 * @param annuity     the annuity.
	 * @return the Installment.
	 */
	private Installment createInstallment(Installment installment, final double ratePercent, final double annuity) {
		final double initPrincipal = installment.getInitialOutstandingPrincipal();
		final double interest = annuityCalculator.calculateAnnuityMonthlyInterest(ratePercent, monthDays,
				initPrincipal);
		installment.setInterest(Double.parseDouble(decimalFormat.format(interest)));
		final double principal = Double.parseDouble(decimalFormat.format(annuity - interest));

		if (principal > initPrincipal)
			installment.setPrincipal(initPrincipal);
		else
			installment.setPrincipal(principal);

		final double remainingPrinicipal = Double.parseDouble(decimalFormat.format(initPrincipal - principal));

		if (remainingPrinicipal < 0) {
			installment.setRemainingOutstandingPrincipal(0.0);
			installment.setBorrowerPaymentAmount(
					Double.parseDouble(decimalFormat.format(principal + interest + remainingPrinicipal)));
		} else {
			installment.setBorrowerPaymentAmount(Double.parseDouble(decimalFormat.format(principal + interest)));
			installment.setRemainingOutstandingPrincipal(remainingPrinicipal);
		}

		return installment;

	}

}
