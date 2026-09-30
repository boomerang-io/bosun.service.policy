package net.boomerangplatform.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PolicyValidation implements Serializable {

  private static final long serialVersionUID = 1L;

  private String policyId;
  private String referenceId;
  private String referenceLink;
  private Map<String, String> labels;
  private Map<String, String> annotations;
  private List<PolicyValidationInput> inputs;

  public String getPolicyId() {
    return policyId;
  }

  public void setPolicyId(String policyId) {
    this.policyId = policyId;
  }

  public String getReferenceId() {
    return referenceId;
  }

  public void setReferenceId(String referenceId) {
    this.referenceId = referenceId;
  }

  public String getReferenceLink() {
    return referenceLink;
  }

  public void setReferenceLink(String referenceLink) {
    this.referenceLink = referenceLink;
  }

  public Map<String, String> getLabels() {
  	if (labels == null) {
  		return new HashMap<String, String>();
  	}
    return labels;
  }

  public void setLabels(Map<String, String> labels) {
    this.labels = labels;
  }

  public Map<String, String> getAnnotations() {
  	if (annotations == null) {
  		return new HashMap<String, String>();
  	}
    return annotations;
  }

  public void setAnnotations(Map<String, String> annotations) {
    this.annotations = annotations;
  }

  public List<PolicyValidationInput> getInputs() {
    return inputs;
  }

  public void setInputs(List<PolicyValidationInput> inputs) {
    this.inputs = inputs;
  }
}
