package com.faraday.coregpx.models;

import java.util.ArrayList;
import java.util.Date;

import io.jenetics.jpx.Point;
import lombok.Data;

@Data
public class Performance {

  private  ArrayList<Point> points;
  private Integer distance;
  private long duration;
  private Date startdate;
  private Date finishdate;
  private String type;  
  
}
