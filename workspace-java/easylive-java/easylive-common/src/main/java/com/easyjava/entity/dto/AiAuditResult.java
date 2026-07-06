package com.easyjava.entity.dto;

public class AiAuditResult {
	private Boolean passed;
	private String riskLevel;
	private String reason;
	private String[] categories;

	public AiAuditResult() {
	}

	public AiAuditResult(Boolean passed, String reason) {
		this.passed = passed;
		this.reason = reason;
	}

	public void setPassed(Boolean passed) {
		this.passed = passed;
	}

	public Boolean getPassed() {
		return this.passed;
	}

	public void setRiskLevel(String riskLevel) {
		this.riskLevel = riskLevel;
	}

	public String getRiskLevel() {
		return this.riskLevel;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return this.reason;
	}

	public void setCategories(String[] categories) {
		this.categories = categories;
	}

	public String[] getCategories() {
		return this.categories;
	}
}
