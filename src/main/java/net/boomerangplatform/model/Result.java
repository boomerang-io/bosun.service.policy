package net.boomerangplatform.model;

import java.util.List;
import java.util.ArrayList;

public class Result {

	private String policyTemplateId;
	private Boolean valid;
	private List<ResultViolation> violations;

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

	public List<ResultViolation> getViolations() {
		if (violations == null) {
			violations = new ArrayList<>();
		}
		return violations;
	}

	public void setViolations(List<ResultViolation> violations) {
		this.violations = violations;
	}
}
