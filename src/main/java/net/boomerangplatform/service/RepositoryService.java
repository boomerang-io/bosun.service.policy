package net.boomerangplatform.service;

import net.boomerangplatform.repository.model.ArtifactSummary;
import net.boomerangplatform.repository.model.DependencyGraph;

public interface RepositoryService {
	DependencyGraph getDependencyGraph(String ciComponentId, String version);
	ArtifactSummary getArtifactSummary(String ciComponentId, String version);
}
