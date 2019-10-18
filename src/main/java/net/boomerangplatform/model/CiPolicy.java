package net.boomerangplatform.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.boomerangplatform.model.CiPolicyConfig;
import net.boomerangplatform.mongo.model.Scope;

public class CiPolicy implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;

  private String name;

  private String teamId;

  private Date createdDate;

  private List<CiPolicyConfig> definitions = new ArrayList<>();

  private List<String> stages = new ArrayList<>();
  
  private Scope scope;

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

  public Date getCreatedDate() {
    return createdDate == null ? null : (Date) createdDate.clone();
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate == null ? null : (Date) createdDate.clone();
  }

  public List<CiPolicyConfig> getDefinitions() {
    return Collections.unmodifiableList(definitions);
  }

  public void setDefinitions(List<CiPolicyConfig> definitions) {
    this.definitions =
        definitions == null ? new ArrayList<>() : new ArrayList<>(definitions);
  }

  public List<String> getStages() {
    return Collections.unmodifiableList(stages);
  }

  public void setStages(List<String> stages) {
    this.stages = stages == null ? new ArrayList<>() : new ArrayList<>(stages);
  }

  public Scope getScope() {
    return scope;
  }

  public void setScope(Scope scope) {
    this.scope = scope;
  }
  
 
}
