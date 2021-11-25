package it.VCUni.parkinsonTestServer.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.VCUni.parkinsonTestServer.exception.UploadFailedException;
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
	 * @throws UploadFailedException 
	 * @throws Exception
	 */
	public void saveAudio(InputStream sample, String samplepath, int testid, boolean tester) throws UploadFailedException {
		
		String location = conn.getFilePath();
		String level;
		try {
			if(tester) {
				level = location+"/raw-test-data/test"+testid;
				File directory = new File(level+"/Parkinson-Disease-Level-0-Healthy");
				directory.mkdirs();
				new File(level+"/Parkinson-Disease-Level-1").mkdir();
				new File(level+"/Parkinson-Disease-Level-2").mkdir();
				new File(level+"/Parkinson-Disease-Level-3").mkdir();
				new File(level+"/Parkinson-Disease-Level-4").mkdir();
				new File(level+"/Parkinson-Disease-Level-5").mkdir();
			}
			else {
				level = location+"/UserAudioTrain/test"+testid;
				File directory = new File(level+"/Parkinson-Disease-Level-0-Healthy");
				directory.mkdirs();
				new File(level+"/Parkinson-Disease-Level-1").mkdir();
				new File(level+"/Parkinson-Disease-Level-2").mkdir();
				new File(level+"/Parkinson-Disease-Level-3").mkdir();
				new File(level+"/Parkinson-Disease-Level-4").mkdir();
				new File(level+"/Parkinson-Disease-Level-5").mkdir();
			}
			File audio = new File(samplepath);
			OutputStream out = new FileOutputStream(audio);
			sample.transferTo(out);
		} catch(Exception e) {throw new UploadFailedException();}
		return;
	}
	
	
	
	/**
	 * @param path
	 */
	public void deleteDoc(String path) {
		File f = new File(path);
		f.delete();
	}

}

