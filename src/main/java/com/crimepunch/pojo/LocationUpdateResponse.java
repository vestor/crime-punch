package com.crimepunch.pojo;

import com.crimepunch.entity.PointEntity;
import lombok.Data;
import lombok.experimental.Builder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manish on 3/10/15.
 */
@Data
@Builder
public class LocationUpdateResponse {

    List<GridPoint> gridPoints = new ArrayList<>();
    List<PointEntity> interestingPoints = new ArrayList<>();
    List<CrimeType> crimeTypes = new ArrayList<>();
}
