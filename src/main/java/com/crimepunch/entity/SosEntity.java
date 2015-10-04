package com.crimepunch.entity;

import com.crimepunch.pojo.Location;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by manish on 4/10/15.
 */
@TypeAlias("Sos")
@Document(collection = "sos")
@Data
public class SosEntity {

    @Id
    ObjectId id;

    @GeoSpatialIndexed
    Location location;

    @Indexed
    String requestingUserId;

    @Indexed
    Boolean isValid;
}
