package net.boomerangplatform.entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import net.boomerangplatform.model.Results;

@Document(collection = "ci_policies_activities")
public class CiPolicyActivityEntity {

	@Id
	private String id;
	private String ciTeamId;
	private String ciComponentActivityId;
	private String ciPolicyId;
	private Date createdDate;
	private List<Results> results;
	private Boolean valid;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCiTeamId() {
		return ciTeamId;
	}

	public void setCiTeamId(String ciTeamId) {
		this.ciTeamId = ciTeamId;
	}

	public String getCiPolicyId() {
		return ciPolicyId;
	}

	public void setCiPolicyId(String ciPolicyId) {
		this.ciPolicyId = ciPolicyId;
	}

	public String getCiComponentActivityId() {
		return ciComponentActivityId;
	}

	public void setCiComponentActivityId(String ciComponentActivityId) {
		this.ciComponentActivityId = ciComponentActivityId;
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
