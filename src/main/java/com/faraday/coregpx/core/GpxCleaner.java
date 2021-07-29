package com.faraday.coregpx.core;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import io.jenetics.jpx.GPX;

import io.jenetics.jpx.WayPoint;

import lombok.extern.java.Log;

@Log
public class GpxCleaner {

  public String clean(String inputPathFileDir, String inputPathFileName, int associationId, int runnersId) {

    // Instantiate the Factory
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    
    List<WayPoint> points = new ArrayList<WayPoint>();

    String outputFileName = "";

    try {

      // optional, but recommended
      // process XML securely, avoid attacks like XML External Entities (XXE)
      dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

      // parse XML file
      DocumentBuilder db = dbf.newDocumentBuilder();

      Document doc = db.parse(inputPathFileName);
      
      // optional, but recommended
      // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
      doc.getDocumentElement().normalize();

      Node trkseg  = doc.getElementsByTagName("trkseg").item(0);
      NodeList trkpts = trkseg.getChildNodes();
      for (int j = 0; j < trkpts.getLength(); j++) {

        Node trkpt = trkpts.item(j);
        if (trkpt.getNodeType() == Node.ELEMENT_NODE) {
          Element trkptElement = (Element) trkpt;
          Double lat = Double.parseDouble(trkptElement.getAttribute("lat"));
          Double lon = Double.parseDouble(trkptElement.getAttribute("lon"));
          ZonedDateTime time = ZonedDateTime.parse(trkptElement.getElementsByTagName("time").item(0).getTextContent());
          log.info("lat:"+ lat + "  lon:"+ lon + " time:" +time);

          final WayPoint point = WayPoint.builder()
          .lat(lat)
          .lon(lon)
          .time(time)
          .build();
          points.add(point);
        }
        
      }
   
    } catch (ParserConfigurationException | SAXException | IOException e) {
      e.printStackTrace();
    }
     
    final GPX gpx = GPX.builder()
    .addTrack(track -> track.addSegment(segment -> {
      points.forEach(point -> segment.addPoint(point));
    })).build();
    try{
      outputFileName = new SimpleDateFormat(associationId+"_"+runnersId+"_yyyyMMddhhmmss'.gpx'").format(new Date());
      OutputStream fileName = new FileOutputStream(inputPathFileDir+outputFileName);
      GPX.write(gpx, fileName);
      
    } catch (SecurityException | IOException e) {
      e.printStackTrace();
    }
    return outputFileName;
  }

}