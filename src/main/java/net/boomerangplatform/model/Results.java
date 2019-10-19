package net.boomerangplatform.model;

import java.util.List;
import java.util.ArrayList;

public class Results {

	private String policyDefinitionId;
	private Boolean valid;
	private List<ResultsViolation> violations;

	public String getPolicyDefinitionId() {
		return policyDefinitionId;
	}

	public void setPolicyDefinitionId(String policyDefinitionId) {
		this.policyDefinitionId = policyDefinitionId;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public List<ResultsViolation> getViolations() {
		if (violations == null) {
			violations = new ArrayList<ResultsViolation>();
		}
		return violations;
	}

	public void setViolations(List<ResultsViolation> violations) {
		this.violations = violations;
	}
}
