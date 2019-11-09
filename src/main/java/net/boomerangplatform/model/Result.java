package net.boomerangplatform.model;

import java.util.List;
import java.util.ArrayList;

public class Result {

	private String policyTemplateId;
	private Boolean valid;
	private List<ResultsViolation> violations;

	public Result() {
    	//do nothing
	}

  public String getPolicyTemplateId() {
		return policyTemplateId;
	}

	public void setPolicyTemplateId(String policyTemplateId) {
		this.policyTemplateId = policyTemplateId;
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
