package com.crimepunch.service;

import com.crimepunch.entity.GridPointEntity;
import com.crimepunch.entity.PointEntity;
import com.crimepunch.pojo.*;
import com.crimepunch.repository.GridPointRepository;
import com.crimepunch.repository.PointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by manish on 3/10/15.
 */
@Component
public class PreProcessingServiceImpl implements PreProcessingService{


    @Autowired
    PointRepository pointRepository;

    @Autowired
    GridPointRepository gridPointRepository;

    @Value("#{'${initLocation}'.split(',')}")
    List<Double> initLocation;

    @Autowired
    MongoTemplate mongoTemplate;


    @PostConstruct
    public void evaluateGridPoints() {
        if(gridPointRepository.count() == 0) {
            Location point1 = LocationBuilder.aLocation()
                    .latitude(initLocation.get(0) - (Constants.RECTANGLE_HALF_SIDE))
                    .longitude(initLocation.get(1) - (Constants.RECTANGLE_HALF_SIDE)).build();
            Location point4 = LocationBuilder.aLocation()
                    .latitude(initLocation.get(0) +(Constants.RECTANGLE_HALF_SIDE))
                    .longitude(initLocation.get(1) + (Constants.RECTANGLE_HALF_SIDE)).build();


            //We now have a rectangle of a side of 20 kms. Now we will compute the grid points of 100m side
            Double currentX = point1.getLatitude();
            Double currentY;
            while(currentX < point4.getLatitude()) {

                List<GridPointEntity> column = new ArrayList<>();
                currentY = point1.getLongitude();
                while(currentY < (point4.getLongitude())) {
                    GridPointEntity gridPointEntity = new GridPointEntity();
                    gridPointEntity.setLocation(LocationBuilder.aLocation().latitude(currentX).longitude(currentY).build());
                    column.add(gridPointEntity);
                    currentY = currentY + (Constants.HUNDRED_METRES);
                }
                mongoTemplate.insertAll(column);
                currentX = currentX + (Constants.HUNDRED_METRES);

            }
        }
    }

    @Override
    public void computeGridPointsData(List<PointEntity> crimePoints) {

        if(crimePoints != null && !crimePoints.isEmpty()) {
            pointRepository.save(crimePoints);
        }
        //FIXME make this pageable
        List<PointEntity> pointEntities = mongoTemplate.find(Query.query(Criteria.where("pointType").is(PointType.CRIME)),PointEntity.class);

        for(PointEntity pointEntity : pointEntities) {

            List<GridPointEntity> gridPointEntitiesRed = mongoTemplate.find(Query.query(Criteria.where("location").near(new Point(pointEntity.getLocation().getLatitude(), pointEntity.getLocation().getLongitude())).maxDistance(Constants.RED_ZONE_METRES)),GridPointEntity.class);
            List<GridPointEntity> gridPointEntitiesYellow = mongoTemplate.find(Query.query(Criteria.where("location").near(new Point(pointEntity.getLocation().getLatitude(), pointEntity.getLocation().getLongitude())).maxDistance(Constants.YELLOW_ZONE_METRES)),GridPointEntity.class);
            gridPointEntitiesYellow.removeAll(gridPointEntitiesRed);

            CrimeType pointCrimeType = CrimeType.valueOf((String) pointEntity.getAttributes().get("crimeType"));

            gridPointEntitiesRed.forEach(a -> {
               if(a.getScoreMap().containsKey(pointCrimeType))
                   a.getScoreMap().put(pointCrimeType, a.getScoreMap().get(pointCrimeType) + 1);
               else
                   a.getScoreMap().put(pointCrimeType, 1.0);


                a.getAttributes().put("lastUpdatedAt", new Date());
            });

            gridPointEntitiesYellow.forEach(a -> {
                if(a.getScoreMap().containsKey(pointCrimeType))
                    a.getScoreMap().put(pointCrimeType, a.getScoreMap().get(pointCrimeType) + 0.5);
                else
                    a.getScoreMap().put(pointCrimeType, 0.5);

                a.getAttributes().put("lastUpdatedAt",new Date());
            });
            List<GridPointEntity> finalToSave = new ArrayList<>();
            finalToSave.addAll(gridPointEntitiesRed);
            finalToSave.addAll(gridPointEntitiesYellow);
            gridPointRepository.save(finalToSave);
        }

    }
}
