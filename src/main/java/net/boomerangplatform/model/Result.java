package net.boomerangplatform.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Result implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String ciPolicyDefinitionId;
	private boolean valid;
	private List<ResultViolation> violations;

	public Result() {
		// Do nothing
	}

	public String getCiPolicyDefinitionId() {
		return ciPolicyDefinitionId;
	}

	public void setCiPolicyDefinitionId(String ciPolicyDefinitionId) {
		this.ciPolicyDefinitionId = ciPolicyDefinitionId;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public List<ResultViolation> getViolations() {
		if (violations == null) {
			violations = new ArrayList<ResultViolation>();
		}
		return violations;
	}

	public void setViolations(List<ResultViolation> violations) {
		this.violations = violations;
	}
}
