package net.boomerangplatform.repository.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Artifact {

  @JsonProperty("general")
  private General general;

  @JsonProperty("issues")
  private List<Issue> issues = new ArrayList<>();

  @JsonProperty("licenses")
  private List<License> licenses = new ArrayList<>();

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
    return Collections.unmodifiableList(issues);
  }

  public void setIssues(List<Issue> issues) {
    this.issues = issues == null ? new ArrayList<>() : new ArrayList<>(issues);
  }

  public List<License> getLicenses() {
    return Collections.unmodifiableList(licenses);
  }

  public void setLicenses(List<License> licenses) {
    this.licenses = licenses == null ? new ArrayList<>() : new ArrayList<>(licenses);
  }
}
