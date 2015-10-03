package com.crimepunch.repository;

import com.crimepunch.entity.PointEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by manish on 3/10/15.
 */
public interface PointRepository extends MongoRepository<PointEntity, ObjectId> {

}
