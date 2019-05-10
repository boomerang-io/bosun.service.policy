package net.boomerangplatform.team.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.boomerangplatform.mongo.entity.CiTeamEntity;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CiTeam extends CiTeamEntity {

  private String boomerangTeamName;
  private String boomerangTeamShortname;

  public CiTeam() {
    // Do nothing
  }

  public CiTeam(CiTeamEntity ciTeamEntity) {
    super(ciTeamEntity);
  }

  public CiTeam(CiTeamEntity ciTeamEntity, String boomerangTeamName,
      String boomerangTeamShortname) {
    super(ciTeamEntity);
    this.boomerangTeamName = boomerangTeamName;
    this.boomerangTeamShortname = boomerangTeamShortname;
  }

  public String getBoomerangTeamName() {
    return boomerangTeamName;
  }

  public String getBoomerangTeamShortname() {
    return boomerangTeamShortname;
  }

  public void setBoomerangTeamName(String boomerangTeamName) {
    this.boomerangTeamName = boomerangTeamName;
  }

  public void setBoomerangTeamShortname(String boomerangTeamShortname) {
    this.boomerangTeamShortname = boomerangTeamShortname;
  }

}
