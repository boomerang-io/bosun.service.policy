package net.boomerangplatform.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.boomerangplatform.mongo.model.CiPolicyConfig;

public class CiPolicy implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private String name;

	private String teamId;

	private Date createdDate;

	private List<CiPolicyConfig> definitions = new ArrayList<>();
	
	private List<String> stages;

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

	public void addDefinition(CiPolicyConfig definition) {
		definitions.add(definition);

	}

	public List<String> getStages() {
		if (stages == null) {
			stages = new ArrayList<String>();
		}
		return stages;
	}

	public void setStages(List<String> stages) {
		this.stages = stages;
	}
}
