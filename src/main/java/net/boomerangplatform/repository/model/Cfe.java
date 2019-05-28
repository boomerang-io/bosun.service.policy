package net.boomerangplatform.repository.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Cfe {

  @JsonProperty("cve")
  private String cve;

  @JsonProperty("cwe")
  private List<String> cwe = new ArrayList<>();

  @JsonProperty("cvss_v2")
  private String cvssV2;

  @JsonProperty("cvss_v3")
  private String cvssV3;

  public Cfe() {
    // Do nothing
  }

  public String getCve() {
    return cve;
  }

  public void setCve(String cve) {
    this.cve = cve;
  }

  public List<String> getCwe() {
    return Collections.unmodifiableList(cwe);
  }

  public void setCwe(List<String> cwe) {
    this.cwe = cwe == null ? new ArrayList<>() : new ArrayList<>(cwe);
  }

  public String getCvssV2() {
    return cvssV2;
  }

  public void setCvssV2(String cvssV2) {
    this.cvssV2 = cvssV2;
  }

  public String getCvssV3() {
    return cvssV3;
  }

  public void setCvssV3(String cvssV3) {
    this.cvssV3 = cvssV3;
  }
}
