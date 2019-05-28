package net.boomerangplatform.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CiPolicyInsights implements Serializable {

	private static final long serialVersionUID = 1L;

	private String ciPolicyId;
	private String ciPolicyName;
	private Date ciPolicyCreatedDate;
	private List<CiPolicyActivitiesInsights> insights;
	
	public CiPolicyInsights() {
		
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

	public Date getCiPolicyCreatedDate() {
		return ciPolicyCreatedDate;
	}

	public void setCiPolicyCreatedDate(Date ciPolicyCreatedDate) {
		this.ciPolicyCreatedDate = ciPolicyCreatedDate;
	}

	public List<CiPolicyActivitiesInsights> getInsights() {
		if (insights == null) {
			insights = new ArrayList<CiPolicyActivitiesInsights>();
		}
		return insights;
	}

	public void setInsights(List<CiPolicyActivitiesInsights> insights) {
		this.insights = insights;
	}
}
