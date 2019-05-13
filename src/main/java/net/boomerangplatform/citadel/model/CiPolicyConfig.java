package net.boomerangplatform.citadel.model;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.boomerangplatform.mongo.model.Rule;

public class CiPolicyConfig implements Serializable {

  private static final long serialVersionUID = 1L;

  private String ciPolicyDefinitionId;

  private CiPolicyDefinition ciPolicyDefinition;

  private List<Rule> rules;

  @JsonIgnore
  public String getCiPolicyDefinitionId() {
    return ciPolicyDefinitionId;
  }

  @JsonProperty("ciPolicyDefinitionId")
  public void setCiPolicyDefinitionId(String ciPolicyDefinitionId) {
    this.ciPolicyDefinitionId = ciPolicyDefinitionId;
  }

  @JsonProperty("policyDefinition")
  public CiPolicyDefinition getCiPolicyDefinition() {
    return ciPolicyDefinition;
  }

  @JsonIgnore
  public void setCiPolicyDefinition(CiPolicyDefinition ciPolicyDefinition) {
    this.ciPolicyDefinition = ciPolicyDefinition;
  }

  public List<Rule> getRules() {
    return rules;
  }

  public void setRules(List<Rule> rules) {
    this.rules = rules;
  }
  
}
