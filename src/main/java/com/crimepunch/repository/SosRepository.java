package com.crimepunch.repository;

import com.crimepunch.entity.SosEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by manish on 4/10/15.
 */
public interface SosRepository extends MongoRepository<SosEntity, ObjectId> {
}
