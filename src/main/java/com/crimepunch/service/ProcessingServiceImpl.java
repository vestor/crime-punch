package com.crimepunch.service;

import com.crimepunch.entity.GridPointEntity;
import com.crimepunch.entity.PointEntity;
import com.crimepunch.entity.SosEntity;
import com.crimepunch.entity.UserEntity;
import com.crimepunch.pojo.*;
import com.crimepunch.repository.GridPointRepository;
import com.crimepunch.repository.PointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by manish on 3/10/15.
 */
@Component
public class ProcessingServiceImpl implements ProcessingService{

    @Autowired
    GridPointRepository gridPointRepository;

    @Autowired
    PointRepository pointRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    UserService userService;

    @Autowired
    MessageBroadcaster messageBroadcaster;

    Map<Gender,List<CrimeType>> GENDER_BASED_CRIMES = new HashMap<>();





    @PostConstruct
    private void buildMaps() {
        GENDER_BASED_CRIMES.put(Gender.MALE, Arrays.asList(CrimeType.values()));
        GENDER_BASED_CRIMES.put(Gender.FEMALE, Arrays.asList(CrimeType.values()));
    }




    private Criteria getTimeCriteria(Date timeStamp) {
        Calendar c = Calendar.getInstance();
        c.setTime(timeStamp);
        c.add(Calendar.MINUTE, Constants.TIME_RANGE_MINUTES);
        Date timeStampUB = c.getTime();
        c.add(Calendar.MINUTE, -2 * Constants.TIME_RANGE_MINUTES);
        Date timeStampLB =  c.getTime();
        return Criteria.where("attributes.occurredOn").gte(timeStampLB).andOperator(Criteria.where("attributes.occurredOn").lte(timeStampUB));
    }

    private Criteria getGenderCriteria(List<Gender> genders) {
        return Criteria.where("attributes.affected_gender").in(genders);
    }

    private Criteria getAgeCriteria(Integer age) {
        return Criteria.where("attributes.age").gte(age - Constants.AGE_RANGE_YEARS).andOperator(Criteria.where("attributes.age").lte(age + Constants.AGE_RANGE_YEARS));
    }

    private List<CrimeType> getCrimeTypes(UserEntity userEntity) {
        if(userEntity.getAttributes().get("gender") != null) {
            return GENDER_BASED_CRIMES.get(userEntity.getAttributes().get("gender"));
        }
        else{
            return Arrays.asList(CrimeType.values());
        }
    }


    @Override
    public LocationUpdateResponse getUpdatedGridPoints(UserLocationUpdate userLocationUpdate) {
        return getUpdatedGridPoints(userLocationUpdate.getId(), userLocationUpdate.getLocation());
    }

    @Override
    public LocationUpdateResponse getUpdatedGridPoints(UserEntity userEntity,Location location) {
        List<CrimeType> crimeTypes = getCrimeTypes(userEntity);

        Point point = new Point(location.getLatitude(),location.getLongitude());

        List<GridPointEntity> list = mongoTemplate.find(Query.query(Criteria.where("location").near(point).maxDistance(Constants.MAXIMUM_RESOLUTION_METRES)),GridPointEntity.class);

        List<GridPoint> gridPointList = new ArrayList<>();
        list.stream().forEach(a -> {
            a.getScoreMap().keySet().retainAll(crimeTypes);
            Double finalScore = a.getScoreMap().values().stream().mapToDouble(d -> d).sum();
            gridPointList.add(GridPoint.builder().location(a.getLocation()).score(finalScore).build());
        });

        List<PointEntity> interestingPoints = mongoTemplate.find(Query.query(Criteria.where("location").near(point).andOperator(Criteria.where("pointType").ne(PointType.CRIME)).maxDistance(Constants.MAXIMUM_RESOLUTION_METRES)),PointEntity.class);

        return LocationUpdateResponse.builder().crimeTypes(crimeTypes).gridPoints(gridPointList).interestingPoints(interestingPoints).build();
    }

    @Override
    public LocationUpdateResponse getUpdatedGridPoints(String userId, Location location) {
        UserEntity userEntity = userService.getUser(userId);
        return getUpdatedGridPoints(userEntity,location);
    }

    @Override
    public void broadcastSOS(SosEntity sosEntity) throws IOException {
        Point point = new Point(sosEntity.getLocation().getLatitude(), sosEntity.getLocation().getLongitude());
        UserEntity userEntity = userService.getUser(sosEntity.getRequestingUserId());
        String userName = userEntity.getAttributes().get("name") != null ? (String) userEntity.getAttributes().get("name") : userEntity.getId();
        List<PointEntity> pointsNearby = mongoTemplate.find(Query.query(Criteria.where("location").near(point).andOperator(Criteria.where("pointType").ne(PointType.CRIME)).maxDistance(Constants.MAXIMUM_SOS_DISTANCE)),PointEntity.class);
        //Sending out sms to to people
        List<UserEntity> usersToSend = mongoTemplate.find(Query.query(Criteria.where("id").in(pointsNearby.stream().filter(e -> e.getPointType() == PointType.PERSON ).map(f -> (String)f.getAttributes().get("userId")).collect(Collectors.toList()))),UserEntity.class);
        Set<String> gcmIds = usersToSend.stream().filter(b -> b.getAttributes().containsKey("gcmId")).map(a -> (String) a.getAttributes().get("gcmId")).collect(Collectors.toSet());
        System.out.println("gcmIds = " + gcmIds);
        for(String gcmId : gcmIds) {
            messageBroadcaster.sendOutMessage(userName + " needs your help!", sosEntity.getLocation(), gcmId);
        }

    }

}
