package net.boomerangplatform.model;

public class ResultsViolation {

	  private String metric;  
	  private String message;
	  private Boolean valid;
	  
	  public ResultsViolation() {
		  
	  }

	  public String getMetric() {
	    return metric;
	  }

	  public void setMetric(String metric) {
	    this.metric = metric;
	  }

	  public String getMessage() {
	    return message;
	  }

	  public void setMessage(String message) {
	    this.message = message;
	  }

	  public Boolean getValid() {
	    return valid;
	  }

	  public void setValid(Boolean valid) {
	    this.valid = valid;
	  }
}
