package net.boomerangplatform.model;

import java.io.Serializable;

public class CiPolicyViolations implements Serializable {

	private static final long serialVersionUID = 1L;

	private String ciPolicyId;
	private String ciPolicyName;
	private String ciStageId;
	private String ciStageName;
	private String ciComponentId;
	private String ciComponentName;
	private String ciComponentVersionId;
	private String ciComponentVersionName;

	private Integer violations;

	public CiPolicyViolations() {

	}

	public String getCiPolicyId() {
		return ciPolicyId;
	}

	public void setCiPolicyId(String ciPolicyId) {
		this.ciPolicyId = ciPolicyId;
	}

	public String getCiPolicyName() {
		return ciPolicyName;
	}

	public void setCiPolicyName(String ciPolicyName) {
		this.ciPolicyName = ciPolicyName;
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

	public Integer getViolations() {
		return violations;
	}

	public void setViolations(Integer violations) {
		this.violations = violations;
	}
}
