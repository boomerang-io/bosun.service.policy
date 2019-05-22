package net.boomerangplatform.repository.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArtifactSummary {

  @JsonProperty("artifacts")
  private List<Artifact> artifacts;

  public ArtifactSummary() {
    // Do nothing
  }

  public List<Artifact> getArtifacts() {
    if (artifacts == null) {
      artifacts = new ArrayList<>();
    }
    return artifacts;
  }

  public void setArtifacts(List<Artifact> artifacts) {
    this.artifacts = artifacts;
  }
}
