package com.crimepunch.repository;

import com.crimepunch.entity.GridPointEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by manish on 3/10/15.
 */
public interface GridPointRepository extends MongoRepository<GridPointEntity, ObjectId> {

}
