package com.lendico.loan.annuity.scheduler;

import static java.util.stream.IntStream.rangeClosed;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lendico.loan.annuity.calculator.CalculatorService;

@Service
public class PaymentScheduler {

	@Autowired
	private CalculatorService calcService;

	public List<Installment> createScheduler(final String start, final int duration, final double rate,
			final double amount) {

		final double annuity = calcService.getAmountForPeriod(rate / 12, amount, duration);

		final List<Installment> installments = new ArrayList<Installment>();

		final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
		final LocalDateTime dateTime = LocalDateTime.parse(start, formatter);

		// schedule remaining payments for duration.

		rangeClosed(0, duration - 1).boxed().forEach(new Consumer<Integer>() {

			@Override
			public void accept(Integer t) {

				// ZonedDateTime zoned = ZonedDateTime.of(nextMonthSameDateTime, zoneId);

				Installment installment = new Installment();

				Double principal = null;
				if (installments.isEmpty()) {
					// add first
					installment.setDate(dateTime);
					final double interest = calcService.interestForPeriod(rate, 30, amount);

					principal = annuity - interest;
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
					// previous Outstanding Principal
					final int k = t - 1;
					Double initPrincipal = Double.parseDouble(
							new DecimalFormat("##.##").format(installments.get(k).getRemainingOutstandingPrincipal()));
					installment.setInitialOutstandingPrincipal(initPrincipal);

					// Calculate interest.

					final double interest = calcService.interestForPeriod(rate, 30, initPrincipal);
					installment.setInterest(interest);

					// Principal = Annuity - Interest

					principal = Double.parseDouble(new DecimalFormat("##.##").format(annuity - interest));
					installment.setPrincipal(principal);

					// Borrower Payment Amount(Annuity) = Principal + Interest

					installment.setBorrowerPaymentAmount(
							Double.parseDouble(new DecimalFormat("##.##").format(principal + interest)));

					double remainingPrinicipal = Double
							.parseDouble(new DecimalFormat("##.##").format(initPrincipal - principal));
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
