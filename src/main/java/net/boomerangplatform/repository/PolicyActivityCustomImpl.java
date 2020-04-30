package net.boomerangplatform.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
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
    
//  { $sort: { createdDate: -1 } }, { $match: { policyId: $?0 } }, { $group: { _id:
  // $referenceId, documents : { $push: $$ROOT } } }, { $replaceRoot: { newRoot: {
  // $arrayElemAt: [$documents, 0] } } }, { $match: { valid: false } }

    List<AggregationOperation> list = new ArrayList<>();
    list.add(Aggregation.sort(new Sort(Direction.DESC, "createdDate")));
    list.add(Aggregation.match(new Criteria("policyId").is(policyId)));
    list.add(Aggregation.group("$referenceId").push("$$ROOT").as("documents"));
    list.add(Aggregation.replaceRoot(ArrayOperators.ArrayElemAt.arrayOf("documents").elementAt(0)));
    list.add(Aggregation.match(new Criteria("valid").is(false)));

    TypedAggregation<PolicyActivityEntity> agg =
        Aggregation.newAggregation(PolicyActivityEntity.class, list);
    return mongoTemplate
        .aggregate(agg, PolicyActivityEntity.class, PolicyActivityEntity.class)
        .getMappedResults();
  }
}
