package net.boomerangplatform.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.boomerangplatform.entity.PolicyActivityEntity;

public interface PolicyActivityRepository
    extends MongoRepository<PolicyActivityEntity, String>, PolicyActivityCustom {
	
	List<PolicyActivityEntity> findByTeamIdAndValidAndCreatedDateAfter(String teamId, Boolean valid, LocalDateTime date);
//	List<PolicyActivityEntity> findByActivityIdAndValid(String activityId, Boolean valid);
}
