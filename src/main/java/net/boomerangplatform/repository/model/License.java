package net.boomerangplatform.repository.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class License {

  @JsonProperty("name")
  private String name;

  @JsonProperty("full_name")
  private String fullName;

  @JsonProperty("components")
  private List<String> components;

  @JsonProperty("more_info_url")
  private List<String> moreInfoUrl;

  public License() {
    // Do nothing
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public List<String> getComponents() {
    if (components == null) {
      components = new ArrayList<>();
    }
    return components;
  }

  public void setComponents(List<String> components) {
    this.components = components;
  }

  public List<String> getMoreInfoUrl() {
    if (moreInfoUrl == null) {
      moreInfoUrl = new ArrayList<>();
    }
    return moreInfoUrl;
  }

  public void setMoreInfoUrl(List<String> moreInfoUrl) {
    this.moreInfoUrl = moreInfoUrl;
  }
}
