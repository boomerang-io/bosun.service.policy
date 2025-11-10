package net.boomerangplatform.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PolicyValidationInput implements Serializable {

  private static final long serialVersionUID = 1L;

  private String templateId;
  private JsonNode data;

  public String getTemplateId() {
    return templateId;
  }

  public void setTemplateId(String templateId) {
    this.templateId = templateId;
  }

  public JsonNode getData() {
    return data;
  }

  public void setData(JsonNode data) {
    this.data = data;
  }
}
