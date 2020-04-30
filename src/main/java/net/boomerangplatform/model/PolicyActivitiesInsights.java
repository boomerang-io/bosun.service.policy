package net.boomerangplatform.model;

import java.io.Serializable;
import java.util.Date;

public class PolicyActivitiesInsights implements Serializable {

  private static final long serialVersionUID = 1L;

  private String policyActivityId;
  private Date policyActivityCreatedDate;
  private Integer violations;

  public PolicyActivitiesInsights() {
    // Do nothing
  }

  public String getPolicyActivityId() {
    return policyActivityId;
  }

  public void setPolicyActivityId(String policyActivityId) {
    this.policyActivityId = policyActivityId;
  }

  public Date getPolicyActivityCreatedDate() {
    return policyActivityCreatedDate == null ? null : (Date) policyActivityCreatedDate.clone();
  }

  public void setPolicyActivityCreatedDate(Date policyActivityCreatedDate) {
    this.policyActivityCreatedDate =
        policyActivityCreatedDate == null ? null : (Date) policyActivityCreatedDate.clone();
  }

  public Integer getViolations() {
    return violations;
  }

  public void setViolations(Integer violations) {
    this.violations = violations;
  }
}
