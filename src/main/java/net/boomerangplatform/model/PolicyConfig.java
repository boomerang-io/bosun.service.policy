package net.boomerangplatform.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class PolicyConfig implements Serializable {

  private static final long serialVersionUID = 1L;

  private String policyDefinitionId;

  private List<Map<String, String>> rules;
  

  public String getPolicyDefinitionId() {
    return policyDefinitionId;
  }

  public void setPolicyDefinitionId(String policyDefinitionId) {
    this.policyDefinitionId = policyDefinitionId;
  }

  public List<Map<String, String>> getRules() {
    return rules;
  }

  public void setRules(List<Map<String, String>> rules) {
    this.rules = rules;
  }
}
