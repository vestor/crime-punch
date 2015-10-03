package com.crimepunch.pojo;

import lombok.Data;
import lombok.experimental.Builder;

/**
 * Created by manish on 3/10/15.
 */

@Data
@Builder
public class GridPoint {

    Location location;

    Double score;

}
