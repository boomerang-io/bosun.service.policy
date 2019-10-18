package net.boomerangplatform.entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import net.boomerangplatform.model.CiPolicyConfig;
import net.boomerangplatform.mongo.model.Scope;

@Document(collection = "ci_policies")
public class CiPolicyEntity {
	@Id
	private String id;

	private String name;

	private String teamId;

	private Date createdDate;
	
	private Scope scope;

	private List<CiPolicyConfig> definitions;

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

	public List<CiPolicyConfig> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<CiPolicyConfig> definitions) {
		this.definitions = definitions;
	}

  public Scope getScope() {
    return scope;
  }

  public void setScope(Scope scope) {
    this.scope = scope;
  }
	
	
}
