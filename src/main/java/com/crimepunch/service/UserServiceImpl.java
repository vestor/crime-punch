package com.crimepunch.service;

import com.crimepunch.entity.PointEntity;
import com.crimepunch.entity.UserEntity;
import com.crimepunch.pojo.Location;
import com.crimepunch.pojo.PointType;
import com.crimepunch.pojo.UserLocationUpdate;
import com.crimepunch.repository.PointRepository;
import com.crimepunch.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * Created by manish on 3/10/15.
 */
@Component
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    PointRepository pointRepository;

    @Autowired
    MongoTemplate mongoTemplate;

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
}
