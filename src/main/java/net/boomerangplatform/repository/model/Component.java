package net.boomerangplatform.repository.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Component {

	@JsonProperty("component_name")
	private String componentName;
	
	@JsonProperty("component_id")
	private String componentId;
	
	@JsonProperty("package_type")
	private String packageType;
	
	@JsonProperty("created")
	private String created;
	
	@JsonProperty("components")
	private List<Component> components = null;
	
	public Component() {
		
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public String getPackageType() {
		return packageType;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
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
