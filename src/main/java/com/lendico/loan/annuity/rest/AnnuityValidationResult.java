package com.lendico.loan.annuity.rest;

import java.util.List;

public class AnnuityValidationResult {

	private boolean isValid;
	private List<String> messages;
	public AnnuityValidationResult(boolean isValid, List<String> messages) {
		super();
		this.isValid = isValid;
		this.messages = messages;
	}
	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	public List<String> getMessages() {
		return messages;
	}
	
	
}
