package net.boomerangplatform.repository.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import net.boomerangplatform.repository.model.ArtifactSummary;
import net.boomerangplatform.repository.model.DependencyGraph;
import net.boomerangplatform.repository.model.SonarQubeReport;

@Service
public class RepositoryServiceImpl implements RepositoryService {

	private final Logger logger = LogManager.getLogger();

	@Value("${repository.rest.url.base}")
	private String repositoryRestUrlBase;

	@Value("${repository.rest.url.dependencygraph}")
	private String repositoryRestUrlDependencygraph;

	@Value("${repository.rest.url.artifactsummary}")
	private String repositoryRestUrlArtifactsummary;
	
	@Value("${repository.rest.url.sonarqubereport}")
	private String repositoryRestUrlSonarqubereport;
	
	@Autowired
	@Qualifier("internalRestTemplate")
	private RestTemplate restTemplate;

	@Override
	public DependencyGraph getDependencyGraph(String ciComponentId, String version) {

		final HttpHeaders headers = new HttpHeaders();
		final HttpEntity<String> request = new HttpEntity<String>(headers);

		String url = repositoryRestUrlBase + repositoryRestUrlDependencygraph.replace("{ciComponentId}", ciComponentId)
				.replace("{version}", version);

		logger.info("getDependencyGraph() - url: " + url);

		try {
			final ResponseEntity<DependencyGraph> response = restTemplate.exchange(url, HttpMethod.GET, request, DependencyGraph.class);
			return (DependencyGraph) response.getBody();
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
			return new DependencyGraph();
		}
	}

	@Override
	public ArtifactSummary getArtifactSummary(String ciComponentId, String version) {

		final HttpHeaders headers = new HttpHeaders();
		final HttpEntity<String> request = new HttpEntity<String>(headers);

		String url = repositoryRestUrlBase + repositoryRestUrlArtifactsummary.replace("{ciComponentId}", ciComponentId)
				.replace("{version}", version);

		logger.info("getArtifactSummary() - url: " + url);

		try {
			final ResponseEntity<ArtifactSummary> response = restTemplate.exchange(url, HttpMethod.GET, request, ArtifactSummary.class);
			return (ArtifactSummary) response.getBody();
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
			return new ArtifactSummary();
		}
	}
	
	@Override
	public SonarQubeReport getSonarQubeReport(String ciComponentId, String version) {
		
		final HttpHeaders headers = new HttpHeaders();
		final HttpEntity<String> request = new HttpEntity<String>(headers);

		String url = repositoryRestUrlBase + repositoryRestUrlSonarqubereport.replace("{ciComponentId}", ciComponentId)
				.replace("{version}", version);

		logger.info("getSonarQubeReport() - url: " + url);

		try {
			final ResponseEntity<SonarQubeReport> response = restTemplate.exchange(url, HttpMethod.GET, request, SonarQubeReport.class);
			return (SonarQubeReport) response.getBody();
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
			return new SonarQubeReport();
		}
	}
}
