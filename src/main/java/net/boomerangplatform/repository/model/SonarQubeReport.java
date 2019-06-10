package net.boomerangplatform.repository.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SonarQubeReport {

  @JsonProperty("issues")
  private Issues issues;

  @JsonProperty("measures")
  private Measures measures;

  public SonarQubeReport() {
    // Do nothing
  }

  public Issues getIssues() {
    return issues;
  }

  public void setIssues(Issues issues) {
    this.issues = issues;
  }

  public Measures getMeasures() {
    return measures;
  }

  public void setMeasures(Measures measures) {
    this.measures = measures;
  }
}
