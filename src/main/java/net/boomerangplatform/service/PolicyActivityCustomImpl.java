package net.boomerangplatform.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import net.boomerangplatform.entity.PolicyActivityEntity;

public class PolicyActivityCustomImpl implements PolicyActivityCustom {
	
private final MongoTemplate mongoTemplate;
    
    @Autowired
    public PolicyActivityCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

  @Override
  public List<PolicyActivityEntity> findTopDistinctViolationsByPolicyIdAndReferenceId(
      String policyId) {

	  SortOperation sortCreatedDateDesc = Aggregation.sort(new Sort(Direction.ASC, "createdDate"));
	  MatchOperation matchPolicyId = Aggregation.match(new Criteria("policyId").is(policyId));
	  GroupOperation groupByReferenceId = Aggregation.group("referenceId").equals("$referenceId");
	  
//    List<AggregationOperation> list = new ArrayList<AggregationOperation>();
//    list.add(Aggregation.sort(Sort.Direction.ASC, "createdDate"));
//    list.add(Aggregation.group(Criteria.where("created").gt(0)));
//
//    TypedAggregation<PolicyActivityEntity> agg = Aggregation.newAggregation(PolicyActivityEntity.class, list);
//    return mongoTemplate.aggregate(agg, PolicyActivityEntity.class, PolicyActivityEntity.class).getMappedResults();
			  return null;
  }
}
