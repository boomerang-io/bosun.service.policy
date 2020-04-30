package net.boomerangplatform.service;

import java.util.List;

import net.boomerangplatform.model.PolicyTeam;

public interface PolicyTeamService {
	List<PolicyTeam> getAllTeams();
	PolicyTeam getTeamById(String id);
	PolicyTeam getTeamByName(String name);
	PolicyTeam createTeam(PolicyTeam newTeam);
}
