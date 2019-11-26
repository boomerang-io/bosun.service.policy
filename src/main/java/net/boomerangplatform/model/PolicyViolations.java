package net.boomerangplatform.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PolicyViolations implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;
  private String policyId;
  private String policyName;
  private String referenceId;
  private String referenceLink;
  private Map<String, String> labels;
  private Map<String, String> annotations;
  private Date policyActivityCreatedDate;
  private List<String> policyDefinitionTypes;
  private Integer nbrViolations;
  private List<PolicyViolation> violations;

  public PolicyViolations() {
    // Do nothing
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPolicyId() {
    return policyId;
  }

  public void setPolicyId(String policyId) {
    this.policyId = policyId;
  }

  public String getPolicyName() {
    return policyName;
  }

  public void setPolicyName(String ciPolicyName) {
    this.policyName = ciPolicyName;
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

  public Integer getNbrViolations() {
    return nbrViolations;
  }

  public void setNbrViolations(Integer nbrViolations) {
    this.nbrViolations = nbrViolations;
  }

  public List<PolicyViolation> getViolations() {
    if (violations == null) {
      violations = new ArrayList<>();
    }
    return violations;
  }

  public void setViolations(List<PolicyViolation> violations) {
    this.violations = violations;
  }

  public Date getPolicyActivityCreatedDate() {
    return policyActivityCreatedDate == null ? null : (Date) policyActivityCreatedDate.clone();
  }

  public void setPolicyActivityCreatedDate(Date policyActivityCreatedDate) {
    this.policyActivityCreatedDate =
        policyActivityCreatedDate == null ? null : (Date) policyActivityCreatedDate.clone();
  }

  public List<String> getPolicyDefinitionTypes() {
    if (policyDefinitionTypes == null) {
      policyDefinitionTypes = new ArrayList<>();
    }
    return policyDefinitionTypes;
  }

  public void setPolicyDefinitionTypes(List<String> policyDefinitionTypes) {
    this.policyDefinitionTypes = policyDefinitionTypes;
  }
}
