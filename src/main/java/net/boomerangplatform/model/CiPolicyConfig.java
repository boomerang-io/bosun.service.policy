package net.boomerangplatform.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CiPolicyConfig implements Serializable {

  private static final long serialVersionUID = 1L;

  private String ciPolicyDefinitionId;

  private List<Map<String, String>> rules;
  

  public String getCiPolicyDefinitionId() {
    return ciPolicyDefinitionId;
  }

  public void setCiPolicyDefinitionId(String ciPolicyDefinitionId) {
    this.ciPolicyDefinitionId = ciPolicyDefinitionId;
  }

  public List<Map<String, String>> getRules() {
    return rules;
  }

  public void setRules(List<Map<String, String>> rules) {
    this.rules = rules;
  }
}
