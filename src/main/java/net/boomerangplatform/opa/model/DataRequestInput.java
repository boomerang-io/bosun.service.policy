package net.boomerangplatform.opa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataRequestInput {

  @JsonProperty("policy")
  private DataRequestPolicy policy;

  @JsonProperty("data")
  private JsonNode data;

  public DataRequestPolicy getPolicy() {
    return policy;
  }

  public void setPolicy(DataRequestPolicy policy) {
    this.policy = policy;
  }

  public JsonNode getData() {
    return data;
  }

  public void setData(JsonNode data) {
    this.data = data;
  }
}
