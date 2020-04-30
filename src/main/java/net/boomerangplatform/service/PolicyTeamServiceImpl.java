package net.boomerangplatform.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.boomerangplatform.entity.PolicyTeamEntity;
import net.boomerangplatform.model.PolicyTeam;
import net.boomerangplatform.repository.PolicyTeamRepository;

@Service
public class PolicyTeamServiceImpl implements PolicyTeamService {

	@Autowired
	private PolicyTeamRepository teamRepository;

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public List<PolicyTeam> getAllTeams() {
		List<PolicyTeamEntity> allPolicyEntities = teamRepository.findAll();
		return allPolicyEntities.stream().map(PolicyTeamServiceImpl::convertEntityToDomain)
				.collect(Collectors.toList());
		
	}

	@Override
	public PolicyTeam getTeamById(String id) {
		Optional<PolicyTeamEntity> result = teamRepository.findById(id);
		if (result.isPresent()) {
			PolicyTeamEntity policyEntity = result.get();
			return convertEntityToDomain(policyEntity);
		}
		return null;
	}

	@Override
	public PolicyTeam getTeamByName(String name) {
		Optional<PolicyTeamEntity> result = teamRepository.findByName(name);
		if (result.isPresent()) {
			PolicyTeamEntity policyEntity = result.get();
			return  convertEntityToDomain(policyEntity);
			
		}
		return null;
	}

	@Override
	public PolicyTeam createTeam(PolicyTeam newTeam) {
		PolicyTeamEntity newTeamEntity = new PolicyTeamEntity();
		newTeamEntity.setName(newTeam.getName());
		PolicyTeamEntity newEntity = teamRepository.save(newTeamEntity);
		return convertEntityToDomain(newEntity);
	}

	private static PolicyTeam convertEntityToDomain(PolicyTeamEntity entity) {
		PolicyTeam policyTeam = new PolicyTeam();
		policyTeam.setId(entity.getId());
		policyTeam.setName(entity.getName());
		return policyTeam;
	}

	@PostConstruct
	private void postConstruct() {
		LOGGER.info("Creating standard policy team configuration.");
	}
}
