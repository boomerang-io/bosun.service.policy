package net.boomerangplatform.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PolicyInsights implements Serializable {

  private static final long serialVersionUID = 1L;

  private String ciPolicyId;
  private String ciPolicyName;
  private Date ciPolicyCreatedDate;
  private List<PolicyActivitiesInsights> insights = new ArrayList<>();

  public PolicyInsights() {
    // Do nothing
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
    return ciPolicyCreatedDate == null ? null : (Date) ciPolicyCreatedDate.clone();
  }

  public void setCiPolicyCreatedDate(Date ciPolicyCreatedDate) {
    this.ciPolicyCreatedDate =
        ciPolicyCreatedDate == null ? null : (Date) ciPolicyCreatedDate.clone();
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
