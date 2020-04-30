package net.boomerangplatform.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class PolicyValidation {

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
    return labels;
  }

  public void setLabels(Map<String, String> labels) {
    this.labels = labels;
  }

  public Map<String, String> getAnnotations() {
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
