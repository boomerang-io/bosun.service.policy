package net.boomerangplatform.opa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataRequestInput {

  @JsonProperty("policy")
  private DataRequestPolicy policy;

  @JsonProperty("data")
  private DataRequestData data;

  public DataRequestPolicy getPolicy() {
    return policy;
  }

  public void setPolicy(DataRequestPolicy policy) {
    this.policy = policy;
  }

  public DataRequestData getData() {
    return data;
  }

  public void setData(DataRequestData data) {
    this.data = data;
  }
}
