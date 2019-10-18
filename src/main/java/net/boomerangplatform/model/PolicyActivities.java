package net.boomerangplatform.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PolicyActivities {

  private String id;
  private String ciComponentId;
  private String ciVersionId;
  private String ciPolicyId;
  private String ciActivityId;
  private List<Result> results = new ArrayList<>();
  private boolean valid;

  public PolicyActivities() {
    // Do nothing
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCiComponentId() {
    return ciComponentId;
  }

  public void setCiComponentId(String ciComponentId) {
    this.ciComponentId = ciComponentId;
  }

  public String getCiVersionId() {
    return ciVersionId;
  }

  public void setCiVersionId(String ciVersionId) {
    this.ciVersionId = ciVersionId;
  }

  public String getCiPolicyId() {
    return ciPolicyId;
  }

  public void setCiPolicyId(String ciPolicyId) {
    this.ciPolicyId = ciPolicyId;
  }

  public String getCiActivityId() {
    return ciActivityId;
  }

  public void setCiActivityId(String ciActivityId) {
    this.ciActivityId = ciActivityId;
  }

  public List<Result> getResults() {
    return Collections.unmodifiableList(results);
  }

  public void setResults(List<Result> results) {
    this.results = results == null ? new ArrayList<>() : new ArrayList<>(results);
  }

  public boolean isValid() {
    return valid;
  }

  public void setValid(boolean valid) {
    this.valid = valid;
  }
}
