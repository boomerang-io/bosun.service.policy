package net.boomerangplatform.service;

import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import net.boomerangplatform.entity.CiPolicyActivityEntity;

public interface CiPolicyActivityRepository extends MongoRepository<CiPolicyActivityEntity, String> {

	List<CiPolicyActivityEntity> findByCiTeamIdAndValidAndCreatedDateAfter(String ciTeamId, Boolean valid, Date date);
	List<CiPolicyActivityEntity> findByCiComponentActivityIdAndValid(String ciComponentActivityId, Boolean valid);
}
