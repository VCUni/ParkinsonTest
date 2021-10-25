package it.VCUni.parkinsonTestServer;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import it.VCUni.parkinsonTestServer.settings.DbConnectionFile;
import it.VCUni.parkinsonTestServer.settings.IDbConnection;

import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@ComponentScan("it.VCUni.parkinsonTestServer.persistence")
@ComponentScan("it.VCUni.parkinsonTestServer.persistence.users")
@ComponentScan("it.VCUni.parkinsonTestServer.persistence.tests")
@ComponentScan("it.VCUni.parkinsonTestServer.persistence.samples")
@ComponentScan("it.VCUni.parkinsonTestServer.handler")

public class TestSpringContainer {
	
	/**
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@Bean
	public IDbConnection dbSettings() throws JsonParseException, JsonMappingException, IOException {
		IDbConnection conn = new DbConnectionFile("sqlite:test.db", "", "", 
				"D:\\eclipse-jee-2021-06-R-win32-x86_64\\eclipse-workspace\\parkinsonTestServer\\",
				"D:\\Utenti\\vikca\\Progetto tesi\\.venv\\Scripts");
		
		try {
			File directory = new File(conn.getFilePath()); 
			if(!directory.exists()) throw new Exception();
			File directoryTest = new File(conn.getFilePath() + "UserAudioTest");
		    boolean resTest = directoryTest.mkdir();
		    if(resTest) System.out.println("La cartella UserAudioTest è stata creata in " + conn.getFilePath() + ".");
		    else System.out.println("La cartella UserAudioTest esiste già.");
		    File directoryTrain = new File(conn.getFilePath() + "UserAudioTrain");
		    boolean resTrain = directoryTrain.mkdir();
		    if(resTrain) System.out.println("La cartella UserAudioTrain è stata creata in " + conn.getFilePath() + ".");
		    else System.out.println("La cartella UserAudioTrain esiste già.");
	    } catch(Exception e) {
	    	System.err.println("Impossibile creare cartella " + conn.getFilePath() + ", specificare un nuovo percorso in connection.json");
	        e.printStackTrace();
	        return null;
	    }
		return conn;
	}
	
	@Bean
  	public BCryptPasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder();
  	};

		
	@Bean
	public Logger getLogger() {
		return NOPLogger.NOP_LOGGER;
	}
	
}
