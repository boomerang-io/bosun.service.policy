package net.boomerangplatform.entity;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import net.boomerangplatform.model.Results;

@Document(collection = "bosun_activities")
public class PolicyActivityEntity {

  @Id private String id;
  private String teamId;
  private String referenceId;
  private String referenceLink;
  private String policyId;
  private Map<String, String> labels;
  private Map<String, String> annotations;
  private Date createdDate;
  private List<Results> results;
  private Boolean valid;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTeamId() {
    return teamId;
  }

  public void setTeamId(String teamId) {
    this.teamId = teamId;
  }

  public String getReferenceId() {
    return referenceId;
  }

  public void setReferenceId(String referenceId) {
    this.referenceId = referenceId;
  }

  public String getReferenceLink() {
    return referenceLink;
  }

  public void setReferenceLink(String referenceLink) {
    this.referenceLink = referenceLink;
  }

  public String getPolicyId() {
    return policyId;
  }

  public void setPolicyId(String policyId) {
    this.policyId = policyId;
  }

  public Map<String, String> getLabels() {
    return labels;
  }

  public void setLabels(Map<String, String> labels) {
    this.labels = labels;
  }

  public Map<String, String> getAnnotations() {
    return annotations;
  }

  public void setAnnotations(Map<String, String> annotations) {
    this.annotations = annotations;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public List<Results> getResults() {
    return results;
  }

  public void setResults(List<Results> results) {
    this.results = results;
  }

  public Boolean getValid() {
    return valid;
  }

  public void setValid(Boolean valid) {
    this.valid = valid;
  }
}
