package net.boomerangplatform.repository.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Paths {

	@JsonProperty("paths")
	private List<String> paths;
	
	public Paths() {
		
	}

	public List<String> getPaths() {
		if (paths == null) {
			paths = new ArrayList<String>();
		}
		return paths;
	}

	public void setPaths(List<String> paths) {
		this.paths = paths;
	}
}
