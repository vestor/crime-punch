package com.crimepunch.service;

import com.crimepunch.entity.UserEntity;
import com.crimepunch.pojo.Location;
import com.crimepunch.pojo.LocationUpdateResponse;
import com.crimepunch.pojo.UserLocationUpdate;

/**
 * Created by manish on 3/10/15.
 */
public interface ProcessingService {
    public LocationUpdateResponse getUpdatedGridPoints(UserLocationUpdate userLocationUpdate);
    public LocationUpdateResponse getUpdatedGridPoints(UserEntity userEntity,Location location);
    public LocationUpdateResponse getUpdatedGridPoints(String userId,Location location);
}
