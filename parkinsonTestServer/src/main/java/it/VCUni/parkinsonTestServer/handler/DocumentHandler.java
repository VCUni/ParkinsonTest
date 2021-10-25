package it.VCUni.parkinsonTestServer.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.VCUni.parkinsonTestServer.settings.IDbConnection;

// Handler che gestisce il salvataggio dei file audio
/**
 * Handler to manage audio saves.
*/
@Component
public class DocumentHandler {
	
	@Autowired
	IDbConnection conn;
	
	
	/**
	 * @param sample
	 * @param testId
	 * @param sampleString
	 * @param test
	 * @return
	 * @throws Exception
	 */
	public String saveDoc(InputStream sample, int testId, String sampleString, boolean test) throws Exception {
		
		String location = conn.getFilePath();
		Path path;
		if(test) {
			String level = location+"/raw-test-data/test"+testId;
			File directory = new File(level+"/Parkinson-Disease-Level-0-Healthy");
			directory.mkdirs();
			new File(level+"/Parkinson-Disease-Level-1").mkdir();
			new File(level+"/Parkinson-Disease-Level-2").mkdir();
			new File(level+"/Parkinson-Disease-Level-3").mkdir();
			new File(level+"/Parkinson-Disease-Level-4").mkdir();
			new File(level+"/Parkinson-Disease-Level-5").mkdir();
			path = Paths.get(directory.getAbsolutePath(), testId +"."+ sampleString +".wav");
		}
		else path = Paths.get(location, "/UserAudioTrain/", testId +"."+ sampleString +".wav");
		File audio = new File(path.toString());
		OutputStream out = new FileOutputStream(audio);
		sample.transferTo(out);
		
		return path.toString();
	}
	
	
	
	/**
	 * @param path
	 */
	public void deleteDoc(String path) {
		File f = new File(path);
		f.delete();
	}

}

