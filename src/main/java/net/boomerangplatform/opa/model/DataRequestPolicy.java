package net.boomerangplatform.opa.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataRequestPolicy {

  @JsonProperty("id")
  private String id;

  @JsonProperty("key")
  private String key;

  @JsonProperty("name")
  private String name;

  @JsonProperty("description")
  private String description;

  @JsonProperty("order")
  private String order;

  @JsonProperty("rules")
  private List<Map<String, String>> rules = new ArrayList<>();

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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getOrder() {
    return order;
  }

  public void setOrder(String order) {
    this.order = order;
  }

  public List<Map<String, String>> getRules() {
    return Collections.unmodifiableList(rules);
  }

  public void setRules(List<Map<String, String>> rules) {
    this.rules = rules == null ? new ArrayList<>() : new ArrayList<>(rules);
  }
}
