package com.crimepunch.service;

import com.crimepunch.entity.PointEntity;
import com.crimepunch.entity.SosEntity;
import com.crimepunch.entity.UserEntity;
import com.crimepunch.pojo.Location;
import com.crimepunch.pojo.PointType;
import com.crimepunch.pojo.UserLocationUpdate;
import com.crimepunch.repository.PointRepository;
import com.crimepunch.repository.SosRepository;
import com.crimepunch.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by manish on 3/10/15.
 */
@Component
@Slf4j
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    PointRepository pointRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    SosRepository sosRepository;

    @Autowired
    ProcessingService processingService;

    public UserEntity saveUser(UserEntity user) {
        return userRepository.save(user);
    }

    @Override
    public UserEntity getUser(String id) {
        UserEntity userEntity = userRepository.findOne(id);
        if(userEntity == null)
            throw new IllegalStateException("User Not Found");
        else
            return userEntity;
    }

    @Override
    public PointEntity updateUserLocation(String userId, Location location) {
        PointEntity pointEntity = new PointEntity();
        pointEntity.setLocation(location);
        pointEntity.setPointType(PointType.PERSON);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        pointEntity.getAttributes().put("user",userEntity);
        pointEntity.getAttributes().put("userId",userEntity.getId());
        PointEntity p = mongoTemplate.findOne(Query.query(Criteria.where("attributes.userId").is(userEntity.getId())), PointEntity.class);
        if(p  != null)
        {
            pointEntity.setId(p.getId());
        }
        return pointRepository.save(pointEntity);

    }

    @Override
    public PointEntity updateUserLocation(UserLocationUpdate userLocationUpdate) {
        return updateUserLocation(userLocationUpdate.getId(),userLocationUpdate.getLocation());
    }

    @Override
    public void sendOutSOS(UserLocationUpdate userLocationUpdate) throws IOException {
        SosEntity sosEntity = mongoTemplate.findOne(Query.query(Criteria.where("requestingUserId").is(userLocationUpdate.getId()).and("isValid").is(true)), SosEntity.class);
        if(sosEntity != null) {
            log.info("Found an already active SOS Entity {}",sosEntity);

        } else {
            log.info("Creating a new SOS Entity");
            sosEntity = new SosEntity();
            sosEntity.setIsValid(true);
            sosEntity.setRequestingUserId(userLocationUpdate.getId());
        }
        sosEntity.setLocation(userLocationUpdate.getLocation());
        sosRepository.save(sosEntity);
        processingService.broadcastSOS(sosEntity);
    }
}
