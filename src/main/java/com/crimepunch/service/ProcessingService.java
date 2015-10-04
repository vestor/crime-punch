package com.crimepunch.service;

import com.crimepunch.entity.SosEntity;
import com.crimepunch.entity.UserEntity;
import com.crimepunch.pojo.Location;
import com.crimepunch.pojo.LocationUpdateResponse;
import com.crimepunch.pojo.UserLocationUpdate;

import java.io.IOException;

/**
 * Created by manish on 3/10/15.
 */
public interface ProcessingService {
    public LocationUpdateResponse getUpdatedGridPoints(UserLocationUpdate userLocationUpdate);
    public LocationUpdateResponse getUpdatedGridPoints(UserEntity userEntity,Location location);
    public LocationUpdateResponse getUpdatedGridPoints(String userId,Location location);
    public void broadcastSOS(SosEntity sosEntity) throws IOException;
}
