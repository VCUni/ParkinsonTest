package it.VCUni.parkinsonTestServer;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import it.VCUni.parkinsonTestServer.handler.ThreadHandler;
import it.VCUni.parkinsonTestServer.handler.WhiteBoardHandler;
import it.VCUni.parkinsonTestServer.persistence.samples.DatabaseSampleAccess;
import it.VCUni.parkinsonTestServer.persistence.tests.DatabaseTestAccess;
import it.VCUni.parkinsonTestServer.service.TestService;
import it.VCUni.parkinsonTestServer.service.UserAuthService;
import it.VCUni.parkinsonTestServer.service.UserService;

import org.slf4j.Logger;

@SpringBootApplication
@EnableScheduling
@ApplicationPath("services")
public class ParkinsonTestServerApplication extends ResourceConfig {
	public static void main(String[] args) {
		SpringApplication.run(ParkinsonTestServerApplication.class, args);
	}

	
	@Autowired
	public ParkinsonTestServerApplication(DatabaseSampleAccess sampledb ,Logger logger, WhiteBoardHandler whiteboard,
			DatabaseTestAccess testdb, ThreadHandler th) throws Exception {
		
		if(sampledb.all().count()==0) {
			sampledb.createSample("Prima");
			sampledb.createSample("Seconda");
			sampledb.createSample("Terza");
			sampledb.createSample("Quarta");
			sampledb.createSample("Quinta");
			sampledb.createSample("Sesta");
			sampledb.createSample("Settima");
			sampledb.createSample("Ottava");
			sampledb.createSample("Nona");
		}
		
		
		register(TestService.class);
		register(MultiPartFeature.class);
		register(UserService.class);
		register(UserAuthService.class);
		
		whiteboard.initBroker();
	}			
	
}
