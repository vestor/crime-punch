package com.crimepunch.service;

import com.crimepunch.entity.PointEntity;
import com.crimepunch.entity.UserEntity;
import com.crimepunch.pojo.Location;
import com.crimepunch.pojo.UserLocationUpdate;

import java.io.IOException;

/**
 * Created by manish on 3/10/15.
 */
public interface UserService {
    public UserEntity saveUser(UserEntity user);
    public UserEntity getUser(String id);
    public PointEntity updateUserLocation(String userId, Location location);
    public PointEntity updateUserLocation(UserLocationUpdate userLocationUpdate);
    public void sendOutSOS(UserLocationUpdate userLocationUpdate) throws IOException;

}
