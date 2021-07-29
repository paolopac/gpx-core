package com.faraday.coregpx;

import java.io.File;
import java.io.IOException;

import com.faraday.coregpx.core.GpxCleaner;
import com.faraday.coregpx.core.Gpxer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Application {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(Application.class, args);

    GpxCleaner gpxCleaner = new GpxCleaner();

    Gpxer gpxer = new Gpxer();

    String inputPathFileDir = System.getProperty("user.dir")+"/fileUploaded/"+1+"/"+1+"/";
    String inputPathFileName = System.getProperty("user.dir")+"/fileUploaded/"+1+"/"+1+"/"+"21_31km-1h_57m_27sec.gpx";
    gpxer.extract(inputPathFileDir, gpxCleaner.clean(inputPathFileDir,inputPathFileName,1,1));

	}

}
