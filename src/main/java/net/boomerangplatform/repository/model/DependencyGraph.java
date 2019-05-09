package net.boomerangplatform.repository.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DependencyGraph {
	
	@JsonProperty("artifact")
	private ArtifactPackage artifact;
	
	@JsonProperty("components")
	private List<Component> components = null;
	
	public DependencyGraph() {
		
	}

	public ArtifactPackage getArtifact() {
		return artifact;
	}

	public void setArtifact(ArtifactPackage artifact) {
		this.artifact = artifact;
	}

	public List<Component> getComponents() {
		if (components == null) {
			components = new ArrayList<Component>();
		}
		return components;
	}

	public void setComponents(List<Component> components) {
		this.components = components;
	}
}
