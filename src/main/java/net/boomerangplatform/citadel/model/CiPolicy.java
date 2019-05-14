package net.boomerangplatform.citadel.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.boomerangplatform.mongo.model.CiPolicyConfig;

public class CiPolicy implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;

  private String name;

  private String teamId;

  private List<CiPolicyConfig> definitions = new ArrayList<>();

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
    return definitions;
  }

  public void setDefinitions(List<CiPolicyConfig> definitions) {
    this.definitions = definitions;
  }

  public void addDefinition(CiPolicyConfig definition) {
    definitions.add(definition);

  }

}
