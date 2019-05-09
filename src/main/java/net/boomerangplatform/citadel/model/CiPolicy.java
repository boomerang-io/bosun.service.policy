package net.boomerangplatform.citadel.model;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.boomerangplatform.mongo.model.Property;

public class CiPolicy implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;

  private String name;

  private String teamId;

  private String ciPolicyDefinitionId;
  
  private CiPolicyDefinition ciPolicyDefinition;

  private List<Property> rules;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTeamId() {
    return teamId;
  }

  public void setTeamId(String teamId) {
    this.teamId = teamId;
  }

  @JsonIgnore
  public String getCiPolicyDefinitionId() {
    return ciPolicyDefinitionId;
  }

  @JsonProperty("templateId")
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

  public List<Property> getRules() {
    return rules;
  }

  public void setRules(List<Property> rules) {
    this.rules = rules;
  }


}
