package net.boomerangplatform.model;

import java.util.List;
import java.util.ArrayList;

public class Results {

	private String ciPolicyDefinitionId;
	private Boolean valid;
	private List<ResultsViolation> violations;

	public String getCiPolicyDefinitionId() {
		return ciPolicyDefinitionId;
	}

	public void setCiPolicyDefinitionId(String ciPolicyDefinitionId) {
		this.ciPolicyDefinitionId = ciPolicyDefinitionId;
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
