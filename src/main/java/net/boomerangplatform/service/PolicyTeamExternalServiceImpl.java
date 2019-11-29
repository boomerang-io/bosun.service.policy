package net.boomerangplatform.service;

import java.util.LinkedList;
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

import net.boomerangplatform.model.InternalPolicyTeam;
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

		ResponseEntity<List<InternalPolicyTeam>> response = restTemplate.exchange(boomerangTeamURL, HttpMethod.GET, request,
				new ParameterizedTypeReference<List<InternalPolicyTeam>>() {
				});
		List<InternalPolicyTeam> allTeams = response.getBody();	
		List<PolicyTeam> policyTeams = new LinkedList<>();
		for (InternalPolicyTeam team : allTeams) {
			PolicyTeam policyTeam = new PolicyTeam();
			policyTeam.setId(team.getId());
			policyTeam.setName(team.getBoomerangTeamName());
			policyTeams.add(policyTeam);
		}
		
		return policyTeams;
	}

	@Override
	public PolicyTeam getTeamById(String id) {
		List<PolicyTeam> allTeams = this.getAllTeams();
		if (allTeams != null) {
		return allTeams.stream().filter(t -> t.getId().equals(id)).findAny().orElse(null);
			
		}
		return null;
	}

	@Override
	public PolicyTeam getTeamByName(String name) {
		List<PolicyTeam> allTeams = this.getAllTeams();
		if (allTeams != null) {
			return allTeams.stream().filter(t -> t.getName().equals(name)).findAny().orElse(null);
		
		}
		return null;
	}

	@Override
	public PolicyTeam createTeam(PolicyTeam newTeam) {
		throw new UnsupportedOperationException("Unable to create teams for an external system.");
	}

	private HttpHeaders buildHeaders() {
		String authToken = restControllerConfig.getCurrentAuthHeader();
		LOGGER.info("Auth Token: " + authToken);
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
