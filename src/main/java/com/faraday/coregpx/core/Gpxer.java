package com.faraday.coregpx.core;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import com.faraday.coregpx.models.Performance;

import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import io.jenetics.jpx.GPX;

import io.jenetics.jpx.Point;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.WayPoint;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.geom.Geoid;

import lombok.extern.java.Log;

@Log
@Component
public class Gpxer {

  private List<Performance> halfFishbone = new ArrayList<Performance>();

  private long duration = 0;
  private int distance = 0;

  public void extract(String inputPathFileDir, String inputPathFileName) throws IOException {

    // Filtering empty tracks and track-segments.
    final GPX gpx3 = GPX.read(inputPathFileDir+"/"+inputPathFileName).toBuilder()
    .trackFilter()
        .map(track -> track.toBuilder()
            .filter(TrackSegment::nonEmpty)
            .build())
        .filter(Track::nonEmpty)
        .build()
    .build();

    List<WayPoint> wayPoints = gpx3.tracks()
    .flatMap(Track::segments)
    .findFirst()
    .map(TrackSegment::points).orElse(Stream.empty())
    .collect(Collectors.toList());


    Date startdate = Date.from(wayPoints.get(0).getTime().orElse(null).toInstant());
    
    if(null == startdate) {
      // MANAGE EXCEPTION: BINDING. INVALID FILE
    }
    int wayPointsSize = wayPoints.size();
    for(int i=0; i<wayPointsSize; i++) {

      ArrayList<Point> points = new ArrayList<Point>();   
      
      for(int j=0;j<=i; j++){
        points.add(wayPoints.get(j));
      }

      Date finishdate =  Date.from(points.get(i).getTime().orElse(null).toInstant());

      if(null == finishdate) {
        // MANAGE EXCEPTION: BINDING. INVALID FILE
      }

      distance = points.stream().collect(Geoid.WGS84.toPathLength()).intValue();  
     

      duration = TimeUnit.MILLISECONDS.toSeconds(finishdate.getTime() - startdate.getTime());

      if(distance >= 5000 && distance <= 5020) { 
        if(!halfFishbone.stream().anyMatch(p -> p.getType().equals("5k"))) {
          String type = "5k";   
          halfFishbone.add(this.setPerformance(points,  distance, duration, startdate, finishdate, type));
        }
      }
      if(distance >= 10000 && distance <= 10020) {
        if(!halfFishbone.stream().anyMatch(p -> p.getType().equals("10k"))) {
         String type = "10k"; 
         halfFishbone.add(this.setPerformance(points,  distance, duration, startdate, finishdate, type));
        }
      }
      if(distance >= 21098 && distance <= 21118) {
        if(!halfFishbone.stream().anyMatch(p -> p.getType().equals("HM"))) {
          String type  = "HM"; 
          halfFishbone.add(this.setPerformance(points,  distance, duration, startdate, finishdate, type));
        }
      }
      if(distance >=  42195 && distance <= 42215) {
        if(!halfFishbone.stream().anyMatch(p -> p.getType().equals("M"))) {
          String type  = "M"; 
          halfFishbone.add(this.setPerformance(points,  distance, duration, startdate, finishdate, type));
        }
      }
      if(i == wayPointsSize-1) {
        if(!halfFishbone.stream().anyMatch(p -> p.getType().equals("MAX"))) {
          String type  = "MAX"; 
          halfFishbone.add(this.setPerformance(points,  distance, duration, startdate, finishdate, type));
        }
      }
    }
  }


  private Performance setPerformance(ArrayList<Point> points, Integer distance,long duration,Date startdate,Date finishdate,String type){
    Performance performance = new Performance();
    performance.setPoints(points);
    performance.setDistance(distance);
    performance.setDuration(duration);
    performance.setStartdate(startdate);
    performance.setFinishdate(finishdate);
    performance.setType(type);
    halfFishbone.add(performance);

    log.info(
      " distance: " + performance.getDistance() 
    + " duration: "+ performance.getDuration()
    + " startdate: "+ performance.getStartdate()
    + " finishdate: "+ performance.getFinishdate()
    + " type: "+ performance.getType());

    return performance;
  }

  
}