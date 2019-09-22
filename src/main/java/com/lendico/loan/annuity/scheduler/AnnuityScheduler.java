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

	@Value(value = "${time.zone}")
	private String timeZone;

	private AnnuityCalculator annuityCalculator;

	@Autowired
	public AnnuityScheduler(AnnuityCalculator annuityCalculator) {
		this.annuityCalculator = annuityCalculator;
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

		logger.info("Start Date {}, Duration {}, Nominal Rate {}, Loan Amount {}", start, duration, rate, amount);

		final Double ratePercent = rate / 100;

		final double annuity = annuityCalculator.calculateAnnuityAmount(ratePercent / yrMonths, amount, duration);

		// can only happen at runtime due to unpredictable reason.
		if (Double.isInfinite(annuity)) {
			logger.error("Divide by zero detected");
			throw new DivideByZeroException();
		}

		final LocalDateTime dateTime = LocalDateTime.parse(start, DateTimeFormatter.ISO_DATE_TIME);
		final ZoneId zoneId = ZoneId.of(timeZone);
		final List<Installment> installments = new ArrayList<Installment>();

		rangeClosed(0, duration - 1).boxed().forEachOrdered(new Consumer<Integer>() {

			@Override
			public void accept(Integer t) {

				final Installment installment = new Installment();
				if (t == 0) {// schedule first payment.
					installment.setDate(dateTime.atZone(zoneId));
					installment.setInitialOutstandingPrincipal(
							Double.parseDouble(new DecimalFormat(decFormat).format(amount)));

				} else {
					installment.setDate(dateTime.plus(t, MONTHS).atZone(zoneId));
					// initial principal is previous remaining principal.
					installment.setInitialOutstandingPrincipal(
							installments.get(installments.size() - 1).getRemainingOutstandingPrincipal());

				}
				installments.add(t, createInstallment(installment, ratePercent, annuity));

			}

		});

		return installments;
	}

	/**
	 * 
	 * @param installment
	 * @param ratePercent
	 * @param annuity
	 * @return
	 */
	Installment createInstallment(Installment installment, final double ratePercent, final double annuity) {
		final double initPrincipal = installment.getInitialOutstandingPrincipal();
		final double interest = annuityCalculator.calculateAnnuityMonthlyInterest(ratePercent, monthDays,
				initPrincipal);
		installment.setInterest(Double.parseDouble(new DecimalFormat(decFormat).format(interest)));
		final double principal = Double.parseDouble(new DecimalFormat(decFormat).format(annuity - interest));

		if (principal > initPrincipal)
			installment.setPrincipal(initPrincipal);
		else
			installment.setPrincipal(principal);

		final double remainingPrinicipal = Double
				.parseDouble(new DecimalFormat(decFormat).format(initPrincipal - principal));

		if (remainingPrinicipal < 0) {
			installment.setRemainingOutstandingPrincipal(0.0);
			installment.setBorrowerPaymentAmount(Double
					.parseDouble(new DecimalFormat(decFormat).format(principal + interest + remainingPrinicipal)));
		} else {
			installment.setBorrowerPaymentAmount(
					Double.parseDouble(new DecimalFormat(decFormat).format(principal + interest)));
			installment.setRemainingOutstandingPrincipal(remainingPrinicipal);
		}

		return installment;

	}

}
