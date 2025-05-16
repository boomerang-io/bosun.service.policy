package net.boomerangplatform.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import net.boomerangplatform.entity.PolicyActivityEntity;

@Repository
public interface PolicyActivityRepository
    extends MongoRepository<PolicyActivityEntity, String>, PolicyActivityCustom {
	
	List<PolicyActivityEntity> findByTeamIdAndValidAndCreatedDateAfter(String teamId, Boolean valid, LocalDateTime date);

}
