package com.crimepunch.service;

import com.crimepunch.entity.PointEntity;

import java.util.List;

/**
 * Created by manish on 3/10/15.
 */
public interface PreProcessingService {

    public void computeGridPointsData(List<PointEntity> pointEntityList);
}
