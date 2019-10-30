package net.boomerangplatform.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PolicyActivities {

  private String id;
  private String teamId;
  private String referenceId;
  private String referenceLink;
  private String policyId;
  private Map<String, String> labels;
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

  public String getTeamId() {
    return teamId;
  }

  public void setTeamId(String teamId) {
    this.teamId = teamId;
  }

  public String getReferenceId() {
    return referenceId;
  }

  public void setReferenceId(String referenceId) {
    this.referenceId = referenceId;
  }

  public String getReferenceLink() {
    return referenceLink;
  }

  public void setReferenceLink(String referenceLink) {
    this.referenceLink = referenceLink;
  }

  public String getPolicyId() {
    return policyId;
  }

  public void setPolicyId(String policyId) {
    this.policyId = policyId;
  }

  public Map<String, String> getLabels() {
    return labels;
  }

  public void setLabels(Map<String, String> labels) {
    this.labels = labels;
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
