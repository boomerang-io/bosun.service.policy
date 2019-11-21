package net.boomerangplatform.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InternalPolicyTeam {
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBoomerangTeamName() {
		return boomerangTeamName;
	}
	public void setBoomerangTeamName(String boomerangTeamName) {
		this.boomerangTeamName = boomerangTeamName;
	}
	private String name;
	private String boomerangTeamName;
	private String id;

}
