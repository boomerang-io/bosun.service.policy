package net.boomerangplatform.repository.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Artifact {

  @JsonProperty("general")
  private General general;

  @JsonProperty("issues")
  private List<Issue> issues;

  @JsonProperty("licenses")
  private List<License> licenses;

  public Artifact() {
    // Do nothing
  }

  public General getGeneral() {
    return general;
  }

  public void setGeneral(General general) {
    this.general = general;
  }

  public List<Issue> getIssues() {
    if (issues == null) {
      issues = new ArrayList<>();
    }
    return issues;
  }

  public void setIssues(List<Issue> issues) {
    this.issues = issues;
  }

  public List<License> getLicenses() {
    if (licenses == null) {
      licenses = new ArrayList<>();
    }
    return licenses;
  }

  public void setLicenses(List<License> licenses) {
    this.licenses = licenses;
  }
}
