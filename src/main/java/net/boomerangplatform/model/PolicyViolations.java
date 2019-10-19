package net.boomerangplatform.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PolicyViolations implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String policyId;
	private String policyName;
	private String ciStageId;
	private String ciStageName;
	private String ciComponentId;
	private String ciComponentName;
	private String ciComponentVersionId;
	private String ciComponentVersionName;
	private Date policyActivityCreatedDate;
	private List<String> policyDefinitionTypes;
	private Integer nbrViolations;
	private List<PolicyViolation> violations;

	public PolicyViolations() {
		// Do nothing
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String ciPolicyName) {
		this.policyName = ciPolicyName;
	}

	public String getCiStageId() {
		return ciStageId;
	}

	public void setCiStageId(String ciStageId) {
		this.ciStageId = ciStageId;
	}

	public String getCiStageName() {
		return ciStageName;
	}

	public void setCiStageName(String ciStageName) {
		this.ciStageName = ciStageName;
	}

	public String getCiComponentId() {
		return ciComponentId;
	}

	public void setCiComponentId(String ciComponentId) {
		this.ciComponentId = ciComponentId;
	}

	public String getCiComponentName() {
		return ciComponentName;
	}

	public void setCiComponentName(String ciComponentName) {
		this.ciComponentName = ciComponentName;
	}

	public String getCiComponentVersionId() {
		return ciComponentVersionId;
	}

	public void setCiComponentVersionId(String ciComponentVersionId) {
		this.ciComponentVersionId = ciComponentVersionId;
	}

	public String getCiComponentVersionName() {
		return ciComponentVersionName;
	}

	public void setCiComponentVersionName(String ciComponentVersionName) {
		this.ciComponentVersionName = ciComponentVersionName;
	}

	public Integer getNbrViolations() {
		return nbrViolations;
	}

	public void setNbrViolations(Integer nbrViolations) {
		this.nbrViolations = nbrViolations;
	}

	public List<PolicyViolation> getViolations() {
		if (violations == null) {
			violations = new ArrayList<PolicyViolation>();
		}
		return violations;
	}

	public void setViolations(List<PolicyViolation> violations) {
		this.violations = violations;
	}

	public Date getPolicyActivityCreatedDate() {
		return policyActivityCreatedDate == null ? null : (Date) policyActivityCreatedDate.clone();
	}

	public void setPolicyActivityCreatedDate(Date policyActivityCreatedDate) {
		this.policyActivityCreatedDate = policyActivityCreatedDate == null ? null
				: (Date) policyActivityCreatedDate.clone();
	}

	public List<String> getPolicyDefinitionTypes() {
		if (policyDefinitionTypes == null) {
			policyDefinitionTypes = new ArrayList<String>();
		}
		return policyDefinitionTypes;
	}

	public void setPolicyDefinitionTypes(List<String> policyDefinitionTypes) {
		this.policyDefinitionTypes = policyDefinitionTypes;
	}
}
