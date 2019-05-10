package net.boomerangplatform.opa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataRequest {

  @JsonProperty("input")
  private DataRequestInput input;

  public DataRequestInput getInput() {
    return input;
  }

  public void setInput(DataRequestInput input) {
    this.input = input;
  }
}
