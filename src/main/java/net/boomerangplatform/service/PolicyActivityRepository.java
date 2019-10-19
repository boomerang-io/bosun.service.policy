package net.boomerangplatform.service;

import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import net.boomerangplatform.entity.PolicyActivityEntity;

public interface PolicyActivityRepository extends MongoRepository<PolicyActivityEntity, String> {

	List<PolicyActivityEntity> findByCiTeamIdAndValidAndCreatedDateAfter(String ciTeamId, Boolean valid, Date date);
	List<PolicyActivityEntity> findByCiComponentActivityIdAndValid(String ciComponentActivityId, Boolean valid);
}
