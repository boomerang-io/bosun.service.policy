package net.boomerangplatform.entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import net.boomerangplatform.model.PolicyDefinition;
import net.boomerangplatform.model.Scope;
import net.boomerangplatform.model.Status;

@Document(collection = "bosun_policies")
public class PolicyEntity {
  @Id private String id;

  private String name;

  private String teamId;

  private Date createdDate;

  private Scope scope;

  private List<PolicyDefinition> definitions;

  private Status status;

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
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public List<PolicyDefinition> getDefinitions() {
    return definitions;
  }

  public void setDefinitions(List<PolicyDefinition> definitions) {
    this.definitions = definitions;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Scope getScope() {
    return scope;
  }

  public void setScope(Scope scope) {
    this.scope = scope;
  }
}
