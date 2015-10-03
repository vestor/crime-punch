package com.crimepunch.repository;

import com.crimepunch.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by manish on 3/10/15.
 */
public interface UserRepository extends MongoRepository<UserEntity, String> {

}
