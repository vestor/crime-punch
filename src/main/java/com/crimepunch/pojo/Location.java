package com.crimepunch.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by manish on 3/10/15.
 */
@Data
public class Location implements Serializable{

    Double latitude;

    Double longitude;
}
