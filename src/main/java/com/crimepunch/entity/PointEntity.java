package com.crimepunch.entity;

import com.crimepunch.pojo.Location;
import com.crimepunch.pojo.PointType;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by manish on 3/10/15.
 */
@TypeAlias("Point")
@Document(collection = "points")
@Data
public class PointEntity {

    @Id
    ObjectId id;

    @GeoSpatialIndexed
    Location location;

    PointType pointType;

    Map<String,Object> attributes = new HashMap<>();

}
