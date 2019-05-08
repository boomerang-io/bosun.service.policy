package net.boomerangplatform.citadel.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CiPolicy implements Serializable{

  private static final long serialVersionUID = 1L;

  private String id;

  private String name;

  private String teamId;

  @JsonProperty("templateId")
  private String ciPolicyDependencyId;

  private List<Map<String, String>> rules;

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

  public String getCiPolicyDependencyId() {
    return ciPolicyDependencyId;
  }

  public void setCiPolicyDependencyId(String ciPolicyDependencyId) {
    this.ciPolicyDependencyId = ciPolicyDependencyId;
  }

  public List<Map<String, String>> getRules() {
    return rules;
  }

  public void setRules(List<Map<String, String>> rules) {
    this.rules = rules;
  }


}
