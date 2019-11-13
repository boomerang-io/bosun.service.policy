package net.boomerangplatform.entity;

import java.util.Date;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import net.boomerangplatform.model.PolicyTemplateConfig;

@Document(collection = "bosun_templates")
public class PolicyTemplateEntity {

  @Id
  private String id;

  private String key;

  private String name;

  private Date createdDate;

  private String description;

  private Integer order;

  private List<PolicyTemplateConfig> config;
  
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

  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }

  public List<PolicyTemplateConfig> getConfig() {
    return config;
  }

  public void setConfig(List<PolicyTemplateConfig> config) {
    this.config = config;
  }

  public String getRego() {
    return rego;
  }

  public void setRego(String rego) {
    this.rego = rego;
  }
}
