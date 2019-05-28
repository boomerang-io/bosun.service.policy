package net.boomerangplatform.repository.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Paths {

  @JsonProperty("paths")
  private List<String> names = new ArrayList<>();

  public Paths() {
    // Do nothing
  }

  public List<String> getNames() {
    return Collections.unmodifiableList(names);
  }

  public void setNames(List<String> names) {
    this.names = names == null ? new ArrayList<>() : new ArrayList<>(names);
  }
}
