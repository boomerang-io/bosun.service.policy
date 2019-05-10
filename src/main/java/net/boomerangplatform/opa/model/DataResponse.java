package net.boomerangplatform.opa.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataResponse {

  @JsonProperty("result")
  private DataResponseResult result;

  public DataResponseResult getResult() {
    return result;
  }

  public void setResult(DataResponseResult result) {
    this.result = result;
  }
}
