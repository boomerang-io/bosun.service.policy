package net.boomerangplatform.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.boomerangplatform.model.PolicyDefinitionConfig;

public class PolicyDefinition implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;

  private String key;

  private String name;

  private Date createdDate;

  private String description;

  private Integer order;

  private List<PolicyDefinitionConfig> config;

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

  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }

  public List<PolicyDefinitionConfig> getConfig() {
    return config == null ? null : Collections.unmodifiableList(config);
  }

  public void setConfig(List<PolicyDefinitionConfig> config) {
    this.config = config == null ? null : new ArrayList<>(config);
  }
}
