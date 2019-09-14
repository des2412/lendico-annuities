package com.lendico.loan.annuity.scheduler;

import static java.util.stream.IntStream.rangeClosed;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.lendico.loan.annuity.calculator.CalculatorService;

@Service
public class PaymentScheduler {
	private static final Logger logger = LoggerFactory.getLogger(PaymentScheduler.class);

	@Value(value = "${year.months}")
	private int yrMonths;

	@Value(value = "${month.days}")
	private int monthDays;

	@Value(value = "${time.zone}")
	private String timeZone;

	@Autowired
	private CalculatorService calcService;

	public List<Installment> createScheduler(final String start, final int duration, final double rate,
			final double amount) {

		logger.info("Start Date {}, Duration {}, Nominal Rate {}, Loan Amount {}", start, duration, rate, amount);

		final double ratePercent = rate / 100;
		final double annuity = calcService.getAmountForPeriod(ratePercent / yrMonths, amount, duration);
		final List<Installment> installments = new ArrayList<Installment>();

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
					ZonedDateTime zBerlin = dateTime.atZone(zoneId);
					installment.setDate(zBerlin);
					final double interest = calcService.interestForPeriod(ratePercent, monthDays, amount);
					principal = annuity - interest;
					installment.setBorrowerPaymentAmount(
							Double.parseDouble(new DecimalFormat("##.##").format(principal + interest)));
					installment.setInitialOutstandingPrincipal(
							Double.parseDouble(new DecimalFormat("##.##").format(amount)));
					installment.setInterest(interest);
					installment.setPrincipal(Double.parseDouble(new DecimalFormat("##.##").format(principal)));
					installment.setRemainingOutstandingPrincipal(amount - principal);

				} else {
					LocalDateTime nextMonthSameDateTime = dateTime.plus(t, ChronoUnit.MONTHS).plusSeconds(0);

					ZonedDateTime zBerlin = nextMonthSameDateTime.atZone(zoneId);
					installment.setDate(zBerlin);
					final int k = t - 1;
					final double initPrincipal = Double.parseDouble(
							new DecimalFormat("##.##").format(installments.get(k).getRemainingOutstandingPrincipal()));

					installment.setInitialOutstandingPrincipal(initPrincipal);

					final double interest = calcService.interestForPeriod(ratePercent, monthDays, initPrincipal);
					installment.setInterest(interest);

					principal = Double.parseDouble(new DecimalFormat("##.##").format(annuity - interest));
					installment.setPrincipal(principal);

					double remainingPrinicipal = Double
							.parseDouble(new DecimalFormat("##.##").format(initPrincipal - principal));

					if (principal > initPrincipal)
						installment.setPrincipal(initPrincipal);

					if (remainingPrinicipal < 0.0) {
						installment.setRemainingOutstandingPrincipal(0.0);
						installment.setBorrowerPaymentAmount(Double.parseDouble(
								new DecimalFormat("##.##").format(principal + interest + remainingPrinicipal)));
					} else {
						installment.setBorrowerPaymentAmount(
								Double.parseDouble(new DecimalFormat("##.##").format(principal + interest)));
						installment.setRemainingOutstandingPrincipal(remainingPrinicipal);
					}

				}
				installments.add(installment);
			}

		});
		return installments;
	}
}
