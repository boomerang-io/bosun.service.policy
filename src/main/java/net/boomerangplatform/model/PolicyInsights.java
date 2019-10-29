package net.boomerangplatform.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PolicyInsights implements Serializable {

  private static final long serialVersionUID = 1L;

  private String policyId;
  private String policyName;
  private Date policyCreatedDate;
  private List<PolicyActivitiesInsights> insights = new ArrayList<>();

  public PolicyInsights() {
    // Do nothing
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

  public void setPolicyName(String policyName) {
    this.policyName = policyName;
  }

  public Date getPolicyCreatedDate() {
    return policyCreatedDate == null ? null : (Date) policyCreatedDate.clone();
  }

  public void setPolicyCreatedDate(Date policyCreatedDate) {
    this.policyCreatedDate =
        policyCreatedDate == null ? null : (Date) policyCreatedDate.clone();
  }

  public List<PolicyActivitiesInsights> getInsights() {
    return Collections.unmodifiableList(insights);
  }

  public void setInsights(List<PolicyActivitiesInsights> insights) {
    this.insights = insights == null ? new ArrayList<>() : new ArrayList<>(insights);
  }

  public void addInsights(PolicyActivitiesInsights insight) {
    insights.add(insight);
  }

  public void removeInsights(PolicyActivitiesInsights insight) {
    insights.remove(insight); // NOSONAR: it is a small list
  }
}
