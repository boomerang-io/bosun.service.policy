package net.boomerangplatform.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class PolicyConfig implements Serializable {

  private static final long serialVersionUID = 1L;

  private String policyTemplateId;

  private List<Map<String, String>> rules;
  

  public String getPolicyTemplateId() {
    return policyTemplateId;
  }

  public void setPolicyTemplateId(String policyTemplateId) {
    this.policyTemplateId = policyTemplateId;
  }

  public List<Map<String, String>> getRules() {
    return rules;
  }

  public void setRules(List<Map<String, String>> rules) {
    this.rules = rules;
  }
}
