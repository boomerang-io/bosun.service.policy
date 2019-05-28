package net.boomerangplatform.repository.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Path {

  @JsonProperty("path")
  private String name;

  public Path() {
    // Do nothing
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
