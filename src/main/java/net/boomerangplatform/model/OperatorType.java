package net.boomerangplatform.model;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OperatorType {
  LESS_THAN("Less Than"),
  GREATER_THAN("Greater Than"),
  EQUALS("Equals"),
  NOT_EQUAL("Does Not Equal"),
  ANY("Any");
  
  @JsonValue
  private String operator;
  
  OperatorType(String operator) {
    this.operator = operator;
  }
  
  public String getOperator() {
    return this.operator;
  }
  
  public static OperatorType getOperatorType(String operator) {
    return Arrays.<OperatorType>asList(values()).stream()
      .filter(value -> value.getOperator().equals(operator)).findFirst().orElse(null);
  }
}
