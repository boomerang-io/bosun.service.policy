package net.boomerangplatform.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PolicyTemplate implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;

  private String key;

  private String name;

  private Date createdDate;

  private String description;

  private String integrationType;

  private List<String> labels;

  private Integer order;

  private List<PolicyTemplateRules> rules;

  private String rego;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getIntegrationType() {
    return integrationType;
  }

  public void setIntegrationType(String integrationType) {
    this.integrationType = integrationType;
  }

  public List<String> getLabels() {
    return labels;
  }

  public void setLabels(List<String> labels) {
    this.labels = labels;
  }

  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }

  public List<PolicyTemplateRules> getRules() {
    return rules == null ? null : Collections.unmodifiableList(rules);
  }

  public void setRules(List<PolicyTemplateRules> rules) {
    this.rules = rules == null ? null : new ArrayList<>(rules);
  }

  public String getRego() {
    return rego;
  }

  public void setRego(String rego) {
    this.rego = rego;
  }
}
