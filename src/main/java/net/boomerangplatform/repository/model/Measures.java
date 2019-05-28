package net.boomerangplatform.repository.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Measures {

  @JsonProperty("ncloc")
  private Integer ncloc;

  @JsonProperty("complexity")
  private Integer complexity;

  @JsonProperty("violations")
  private Integer violations;

  public Measures() {
    // Do nothing
  }

  public Integer getNcloc() {
    return ncloc;
  }

  public void setNcloc(Integer ncloc) {
    this.ncloc = ncloc;
  }

  public Integer getComplexity() {
    return complexity;
  }

  public void setComplexity(Integer complexity) {
    this.complexity = complexity;
  }

  public Integer getViolations() {
    return violations;
  }

  public void setViolations(Integer violations) {
    this.violations = violations;
  }
}
