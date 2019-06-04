package net.boomerangplatform.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.boomerangplatform.mongo.entity.BoomerangTeamEntity;
import net.boomerangplatform.mongo.entity.CiTeamEntity;
import net.boomerangplatform.mongo.entity.HighLevelGroupEntity;
import net.boomerangplatform.mongo.repository.BoomerangTeamRepository;
import net.boomerangplatform.mongo.repository.CiTeamRepository;
import net.boomerangplatform.mongo.service.HighLevelGroupService;
import net.boomerangplatform.team.model.CiTeam;

@Service
public class TeamServiceImpl implements TeamService {

  @Autowired
  private BoomerangTeamRepository boomerangTeamRepository;

  @Autowired
  private CiTeamRepository ciTeamRepository;

  @Autowired
  private HighLevelGroupService hlgEntityService;

  @Override
  public List<CiTeam> getTeams() {
    final List<CiTeam> ciTeams = new ArrayList<>();

    for (final CiTeam ciTeam : getCiTeams(ciTeamRepository.findAll())) {
      final String ciTeamHlgId = ciTeam.getHigherLevelGroupId();
      final HighLevelGroupEntity hlgWithId = hlgEntityService.getHighLevelGroupWithId(ciTeamHlgId);

      if (Boolean.TRUE.equals(ciTeam.getIsActive())
          && Boolean.TRUE.equals(hlgWithId.getIsActive())) {
        ciTeams.add(ciTeam);
      }
    }
    return ciTeams;
  }

  private List<CiTeam> getCiTeams(List<CiTeamEntity> ciTeamEntities) {
    final List<CiTeam> ciTeams = new ArrayList<>();

    for (final CiTeamEntity ciTeamEntity : ciTeamEntities) {
      final BoomerangTeamEntity boomerangTeam =
          boomerangTeamRepository.findById(ciTeamEntity.getHigherLevelGroupId());

      CiTeam ciTeam;

      if (boomerangTeam != null) {
        ciTeam = new CiTeam(ciTeamEntity, boomerangTeam.getName(), boomerangTeam.getShortName());
      } else {
        ciTeam = new CiTeam(ciTeamEntity);
      }

      ciTeams.add(ciTeam);
    }

    return ciTeams;
  }
}
