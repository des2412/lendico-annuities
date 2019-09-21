/**
 * Delegate for creation of Installment instances.
 */
package com.lendico.loan.annuity.scheduler;

import java.text.DecimalFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.lendico.loan.annuity.calculator.AnnuityCalculator;
import com.lendico.loan.annuity.model.Installment;

/**
 * @author dg8wi 
 *
 */
@Component
public class AnnuitySchedulerDelegate {

	@Autowired
	private AnnuityCalculator annuityCalculator;

	@Value(value = "${month.days}")
	private int monthDays;

	@Value(value = "${decimal.format}")
	private String decFormat;

	Installment createInstallment(Installment installment, final double ratePercent, final double annuity) {
		final double initPrincipal = installment.getInitialOutstandingPrincipal();
		final double interest = annuityCalculator.interestForPeriod(ratePercent, monthDays, initPrincipal);
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

	/**
	 * 
	 * @param rate the annuity nominal rate.
	 * @param amount the annuity loan amount.
	 * @param duration the annuity duration.
	 * @return the annuity amount.
	 */
	public double getAnnuityAmount(Double rate, Double amount, Integer duration) {
		return annuityCalculator.getAmountForPeriod(rate, amount, duration);
	}

}
