package net.boomerangplatform.citadel.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CiPolicy implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;

  private String name;

  private String teamId;

  private List<CiPolicyConfig> definitions;

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

  public List<CiPolicyConfig> getDefinitions() {
    return definitions == null ? new ArrayList<>() : definitions;
  }

  public void setDefinitions(List<CiPolicyConfig> definitions) {
    this.definitions = definitions;
  }
  
  public void addDefinition(CiPolicyConfig definition) {
    getDefinitions().add(definition);
  }

}
