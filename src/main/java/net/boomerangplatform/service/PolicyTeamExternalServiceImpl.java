package net.boomerangplatform.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import net.boomerangplatform.model.PolicyTeam;
import net.boomerangplatform.rest.RestControllerConfig;

@Service
@Primary
@ConditionalOnProperty(name = "boomerang.standalone", havingValue = "false")
public class PolicyTeamExternalServiceImpl implements PolicyTeamService {

	private RestTemplate restTemplate = new RestTemplate();

	@Value("${boomerang.teams.url}")
	public String boomerangTeamURL;

	@Autowired
	private RestControllerConfig restControllerConfig;

	private static final Logger LOGGER = LogManager.getLogger();

	public List<PolicyTeam> getAllTeams() {

		final HttpHeaders headers = buildHeaders();
		final HttpEntity<String> request = new HttpEntity<>(headers);

		ResponseEntity<List<PolicyTeam>> response = restTemplate.exchange(boomerangTeamURL, HttpMethod.GET, request,
				new ParameterizedTypeReference<List<PolicyTeam>>() {
				});
		List<PolicyTeam> allTeams = response.getBody();
		return allTeams;
	}

	@Override
	public PolicyTeam getTeamById(String id) {

		final HttpHeaders headers = buildHeaders();
		final HttpEntity<String> request = new HttpEntity<>(headers);

		ResponseEntity<List<PolicyTeam>> response = restTemplate.exchange(boomerangTeamURL, HttpMethod.GET, request,
				new ParameterizedTypeReference<List<PolicyTeam>>() {
				});
		List<PolicyTeam> allTeams = response.getBody();

		if (allTeams != null) {
			PolicyTeam team = allTeams.stream().filter(t -> t.getId().equals(id)).findAny().orElse(null);
			return team;
		}

		return null;
	}

	@Override
	public PolicyTeam getTeamByName(String name) {

		final HttpHeaders headers = buildHeaders();
		final HttpEntity<String> request = new HttpEntity<>(headers);

		ResponseEntity<List<PolicyTeam>> response = restTemplate.exchange(boomerangTeamURL, HttpMethod.GET, request,
				new ParameterizedTypeReference<List<PolicyTeam>>() {
				});
		List<PolicyTeam> allTeams = response.getBody();

		if (allTeams != null) {
			PolicyTeam team = allTeams.stream().filter(t -> t.getName().equals(name)).findAny().orElse(null);
			return team;
		}
		return null;
	}

	@Override
	public PolicyTeam createTeam(PolicyTeam newTeam) {
		throw new UnsupportedOperationException("Unable to create teams for an external system.");
	}

	private HttpHeaders buildHeaders() {
		String authToken = restControllerConfig.getCurrentAuthHeader();
		
		final HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Authorization", authToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}


	@PostConstruct
	private void postConstruct() {
		LOGGER.info("Creating exterrnal policy team configuration.");
	}
}
