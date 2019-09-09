package net.boomerangplatform.opa.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataResponseResultViolation {

  @JsonProperty("metric")
  private String metric;
  
  @JsonProperty("message")
  private String message;

  @JsonProperty("valid")
  private Boolean valid;

public String getMetric() {
return metric;}

public void setMetric(String metric) {
this.metric = metric;}

public String getMessage() {
return message;}

public void setMessage(String message) {
this.message = message;}

public Boolean getValid() {
return valid;}

public void setValid(Boolean valid) {
this.valid = valid;}

}
