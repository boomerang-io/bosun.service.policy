package net.boomerangplatform.model;

import org.springframework.http.HttpStatus;

public class EventStatus {

  private HttpStatus status;
  private String id;
  private String name;
  private String description;
  
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
  public HttpStatus getStatus() {
    return status;
  }
  public void setStatus(HttpStatus status) {
    this.status = status;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  
  
  
  
  
  
}
