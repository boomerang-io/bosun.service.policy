package net.boomerangplatform.opa.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataResponseResult {

  @JsonProperty("detail")
  private DataResponseDetail detail;

  @JsonProperty("valid")
  private Boolean valid;

  public DataResponseDetail getDetail() {
    return detail;
  }

  public void setDetail(DataResponseDetail detail) {
    this.detail = detail;
  }

  public Boolean getValid() {
    return valid;
  }

  public void setValid(Boolean valid) {
    this.valid = valid;
  }
}
