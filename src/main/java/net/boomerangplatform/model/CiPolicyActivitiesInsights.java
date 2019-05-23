package net.boomerangplatform.model;

import java.io.Serializable;
import java.util.Date;

public class CiPolicyActivitiesInsights implements Serializable {

  private static final long serialVersionUID = 1L;

  private String ciPolicyActivityId;
  private Date ciPolicyActivityCreatedDate;
  private Integer violations;

  public CiPolicyActivitiesInsights() {
    // Do nothing
  }

  public String getCiPolicyActivityId() {
    return ciPolicyActivityId;
  }

  public void setCiPolicyActivityId(String ciPolicyActivityId) {
    this.ciPolicyActivityId = ciPolicyActivityId;
  }

  public Date getCiPolicyActivityCreatedDate() {
    return ciPolicyActivityCreatedDate == null ? null : (Date) ciPolicyActivityCreatedDate.clone();
  }

  public void setCiPolicyActivityCreatedDate(Date ciPolicyActivityCreatedDate) {
    this.ciPolicyActivityCreatedDate =
        ciPolicyActivityCreatedDate == null ? null : (Date) ciPolicyActivityCreatedDate.clone();
  }

  public Integer getViolations() {
    return violations;
  }

  public void setViolations(Integer violations) {
    this.violations = violations;
  }
}
