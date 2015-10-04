package com.crimepunch.controller;

import com.crimepunch.entity.PointEntity;
import com.crimepunch.entity.UserEntity;
import com.crimepunch.pojo.LocationBuilder;
import com.crimepunch.pojo.LocationUpdateResponse;
import com.crimepunch.pojo.UserLocationUpdate;
import com.crimepunch.service.PreProcessingService;
import com.crimepunch.service.ProcessingService;
import com.crimepunch.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class AppController {

	@Autowired
	UserService userService;

	@Autowired
	ProcessingService processingService;

	@Autowired
	PreProcessingService preProcessingService;


	@RequestMapping(method = RequestMethod.POST,value = "/save_user" )
	@ResponseBody
	public UserEntity saveUser(@RequestBody UserEntity user) {
		return userService.saveUser(user);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/get_user/{id}")
	@ResponseBody
	public UserEntity getUser(@PathVariable String id) {
		log.info("Called get user {}",id);
		UserEntity userEntity = userService.getUser(id);
		return userEntity;
	}

	@RequestMapping(method = RequestMethod.POST,value = "/update_location")
	@ResponseBody
	public LocationUpdateResponse updateUserPosition(@RequestBody UserLocationUpdate userLocationUpdate){
		userService.updateUserLocation(userLocationUpdate);
		return processingService.getUpdatedGridPoints(userLocationUpdate);
	}

	@RequestMapping(method = RequestMethod.GET,value = "/update_location")
	@ResponseBody
	public UserLocationUpdate getSomething() {
		UserLocationUpdate userLocationUpdate = new UserLocationUpdate();
		userLocationUpdate.setLocation(LocationBuilder.aLocation().longitude(10.00).latitude(12.00).build());
		userLocationUpdate.setId("0");
		return userLocationUpdate;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/update_data")
	@ResponseBody
	public void updateData(@RequestBody(required = false) List<PointEntity> pointData) {
		preProcessingService.computeGridPointsData(pointData);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/sos")
	@ResponseBody
	public Map<String,Object> sendOutSOS(@RequestBody UserLocationUpdate userLocationUpdate) throws IOException {
		userService.sendOutSOS(userLocationUpdate);
		return Collections.singletonMap("value",true);
	}


}