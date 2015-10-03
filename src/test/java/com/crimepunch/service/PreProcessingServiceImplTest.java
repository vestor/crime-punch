package com.crimepunch.service;

import com.crimepunch.entity.PointEntity;
import com.crimepunch.pojo.CrimeType;
import com.crimepunch.pojo.LocationBuilder;
import com.crimepunch.pojo.PointType;
import com.crimepunch.repository.PointRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml")
public class PreProcessingServiceImplTest {

    @Autowired
    public PointRepository pointRepository;


    @Autowired
    public PreProcessingService preProcessingService;

    @Test
    public void putSomeSeedData() {

        for (int i = 0; i < 100; i++) {

            for(CrimeType crimeType : CrimeType.values()) {
                PointEntity pointEntity = new PointEntity();
                pointEntity.setPointType(PointType.CRIME);
                pointEntity.setLocation(LocationBuilder.aLocation().latitude(12.9539974 + (0.001 * (i + crimeType.ordinal()))).longitude(77.6309395 + (0.001 * i)).build());
                pointEntity.setAttributes(Collections.singletonMap("crimeType", crimeType));
                    pointRepository.save(pointEntity);
            }
        }

    }

    @Test
    public void updateData() {
        preProcessingService.computeGridPointsData(null);
    }

}