package net.boomerangplatform.opa.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class DataResponseResult {

  @JsonProperty("detail")
  private JsonNode detail;

  @JsonProperty("valid")
  private Boolean valid;

  public JsonNode getDetail() {
    return detail;
  }

  public void setDetail(JsonNode detail) {
    this.detail = detail;
  }

  public Boolean getValid() {
    return valid;
  }

  public void setValid(Boolean valid) {
    this.valid = valid;
  }
}
