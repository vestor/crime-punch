package com.crimepunch.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by manish on 3/10/15.
 */
@TypeAlias("User")
@Document(collection = "users")
@Data
public class UserEntity {

    @Id
    String id;

    Map<String,Object> attributes = new HashMap<>();

}
