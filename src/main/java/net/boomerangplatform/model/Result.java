package net.boomerangplatform.model;

public class Result {

  private String ciPolicyDefinitionId;
  private boolean valid;
  private String detail;

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

  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }
}
