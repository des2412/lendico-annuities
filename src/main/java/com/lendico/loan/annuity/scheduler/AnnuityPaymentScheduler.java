package com.lendico.loan.annuity.scheduler;

import static java.time.temporal.ChronoUnit.MONTHS;
import static java.util.Objects.requireNonNull;
import static java.util.stream.IntStream.rangeClosed;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.lendico.loan.annuity.calculator.AnnuityCalculator;
import com.lendico.loan.annuity.exception.DivideByZeroException;

@Service
public class AnnuityPaymentScheduler {
	private static final Logger logger = LoggerFactory.getLogger(AnnuityPaymentScheduler.class);

	@Value(value = "${year.months}")
	private int yrMonths;

	@Value(value = "${month.days}")
	private int monthDays;

	@Value(value = "${time.zone}")
	private String timeZone;

	@Value(value = "${decimal.format}")
	private String decFormat;

	private AnnuityCalculator annuityCalculator;

	@Autowired
	public AnnuityPaymentScheduler(AnnuityCalculator calcService) {
		this.annuityCalculator = calcService;
	}

	public List<Installment> createSchedule(final String start, final Integer duration, final Double rate,
			final Double amount) {
		// perform basic null checks.
		requireNonNull(start, "Start date is required");
		requireNonNull(duration, "Duration is required");
		requireNonNull(rate, "Rate is required");
		requireNonNull(amount, "Amount is required");

		logger.info("Start Date {}, Duration {}, Nominal Rate {}, Loan Amount {}", start, duration, rate, amount);

		final double ratePercent = rate / 100;
		final double annuity = annuityCalculator.getAmountForPeriod(ratePercent / yrMonths, amount, duration);

		final List<Installment> installments = new ArrayList<Installment>();
		// can only happen at runtime due to unpredicatable reason.
		if (Double.isInfinite(annuity)) {
			logger.error("Divide by zero detected");
			throw new DivideByZeroException();
		}

		final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
		final LocalDateTime dateTime = LocalDateTime.parse(start, formatter);
		final ZoneId zoneId = ZoneId.of(timeZone);

		// schedule payments for duration.

		rangeClosed(0, duration - 1).boxed().forEach(new Consumer<Integer>() {

			@Override
			public void accept(Integer t) {

				final Installment installment = new Installment();

				Double principal = null;
				if (installments.isEmpty()) {
					installment.setDate(dateTime.atZone(zoneId));
					final double interest = annuityCalculator.interestForPeriod(ratePercent, monthDays, amount);
					principal = annuity - interest;
					installment.setBorrowerPaymentAmount(
							Double.parseDouble(new DecimalFormat(decFormat).format(principal + interest)));
					installment.setInitialOutstandingPrincipal(
							Double.parseDouble(new DecimalFormat(decFormat).format(amount)));
					installment.setInterest(interest);
					installment.setPrincipal(Double.parseDouble(new DecimalFormat(decFormat).format(principal)));
					installment.setRemainingOutstandingPrincipal(amount - principal);

				} else {
					LocalDateTime nextMonthSameDateTime = dateTime.plus(t, MONTHS).plusSeconds(0);
					installment.setDate(nextMonthSameDateTime.atZone(zoneId));
					// reference previous installment.
					final double initPrincipal = Double.parseDouble(new DecimalFormat(decFormat)
							.format(installments.get(--t).getRemainingOutstandingPrincipal()));

					installment.setInitialOutstandingPrincipal(initPrincipal);

					final double interest = annuityCalculator.interestForPeriod(ratePercent, monthDays, initPrincipal);
					installment.setInterest(interest);

					principal = Double.parseDouble(new DecimalFormat(decFormat).format(annuity - interest));
					installment.setPrincipal(principal);

					final double remainingPrinicipal = Double
							.parseDouble(new DecimalFormat(decFormat).format(initPrincipal - principal));

					if (principal > initPrincipal)
						installment.setPrincipal(initPrincipal);

					if (remainingPrinicipal < 0) {
						installment.setRemainingOutstandingPrincipal(0.0);
						installment.setBorrowerPaymentAmount(Double.parseDouble(
								new DecimalFormat(decFormat).format(principal + interest + remainingPrinicipal)));
					} else {
						installment.setBorrowerPaymentAmount(
								Double.parseDouble(new DecimalFormat(decFormat).format(principal + interest)));
						installment.setRemainingOutstandingPrincipal(remainingPrinicipal);
					}

				}
				installments.add(installment);
			}

		});
		return installments;
	}

}
