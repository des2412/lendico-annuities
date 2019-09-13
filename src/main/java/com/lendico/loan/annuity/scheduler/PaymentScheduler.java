package com.lendico.loan.annuity.scheduler;

import static java.util.stream.IntStream.rangeClosed;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lendico.loan.annuity.calculator.CalculatorService;

@Service
public class PaymentScheduler {
	private static final Logger logger = LoggerFactory.getLogger(PaymentScheduler.class);
	@Autowired
	private CalculatorService calcService;

	public List<Installment> createScheduler(final String start, final int duration, final double rate,
			final double amount) {

		logger.info("Start Date {}, Duration {}, Nominal Rate {}, Loan Amount {}", start, duration, rate, amount);

		final double ratePercent = rate / 100;
		logger.info("Rate percent format {}", ratePercent);
		final double annuity = calcService.getAmountForPeriod(ratePercent / 12, amount, duration);
		logger.info("Annuity {}", annuity);
		final List<Installment> installments = new ArrayList<Installment>();

		final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
		final LocalDateTime dateTime = LocalDateTime.parse(start, formatter);

		// schedule remaining payments for duration.

		rangeClosed(0, duration - 1).boxed().forEach(new Consumer<Integer>() {

			@Override
			public void accept(Integer t) {

				final Installment installment = new Installment();

				Double principal = null;
				if (installments.isEmpty()) {
					installment.setDate(dateTime);
					final double interest = calcService.interestForPeriod(ratePercent, 30, amount);
					logger.info("Initial Payment Interest {}", interest);
					principal = annuity - interest;
					logger.info("Initial Payment Principal {}", principal);

					installment.setBorrowerPaymentAmount(
							Double.parseDouble(new DecimalFormat("##.##").format(principal + interest)));
					installment.setDate(dateTime);
					installment.setInitialOutstandingPrincipal(
							Double.parseDouble(new DecimalFormat("##.##").format(amount)));
					installment.setInterest(interest);
					installment.setPrincipal(Double.parseDouble(new DecimalFormat("##.##").format(principal)));
					installment.setRemainingOutstandingPrincipal(amount - principal);

				} else {
					LocalDateTime nextMonthSameDateTime = dateTime.plus(t, ChronoUnit.MONTHS);
					installment.setDate(nextMonthSameDateTime);
					final int k = t - 1;
					final double initPrincipal = Double.parseDouble(
							new DecimalFormat("##.##").format(installments.get(k).getRemainingOutstandingPrincipal()));
					logger.info("Payment amount {} Payment {}", initPrincipal, k + 2);
					installment.setInitialOutstandingPrincipal(initPrincipal);

					final double interest = calcService.interestForPeriod(ratePercent, 30, initPrincipal);
					logger.info("Payment {} Interest {}", k + 2, interest);
					installment.setInterest(interest);

					principal = Double.parseDouble(new DecimalFormat("##.##").format(annuity - interest));
					logger.info("Payment {} Principal {} ", k + 2, principal);
					installment.setPrincipal(principal);

					installment.setBorrowerPaymentAmount(
							Double.parseDouble(new DecimalFormat("##.##").format(principal + interest)));

					logger.info("Payment {} amount (Principal + Interest) {} ", k + 2, principal + interest);

					double remainingPrinicipal = Double
							.parseDouble(new DecimalFormat("##.##").format(initPrincipal - principal));
					logger.info("Outstanding principal {} Payment {}", principal, k + 2);
					installment.setRemainingOutstandingPrincipal(remainingPrinicipal);

					if (principal > initPrincipal)
						installment.setPrincipal(initPrincipal);

					if (remainingPrinicipal < 0.0)
						installment.setRemainingOutstandingPrincipal(0.0);

				}
				installments.add(installment);
			}

		});
		return installments;
	}
}
