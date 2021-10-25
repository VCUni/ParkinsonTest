package it.VCUni.parkinsonTestServer.handler;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.sql.SQLException;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.VCUni.parkinsonTestServer.entity.Test;
import it.VCUni.parkinsonTestServer.exception.DBException;
import it.VCUni.parkinsonTestServer.exception.TestNotCompletedException;
import it.VCUni.parkinsonTestServer.exception.TestNotFoundException;
import it.VCUni.parkinsonTestServer.interfaces.TestAccess;
import it.VCUni.parkinsonTestServer.settings.IDbConnection;

/**
 *  Handler for thread's management
*/
@Component
public class ThreadHandler {
	
	@Autowired
	TestAccess tests;
	
	@Autowired
	Logger log;
	
	@Autowired
	IDbConnection conn;
	
	@Autowired
	WhiteBoardHandler whiteboard;
	
	
	public List<Integer> pendingTest = null;
	List<ThreadScript> threads = new ArrayList<ThreadScript>();
	
	@Scheduled(fixedDelay = 3000)
	public void run() {
		if(pendingTest==null) {
		try {
			pendingTest = tests.getPendingTests();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		log.info("ThreadHandler starts");
		}
			int i = 0;
			if(threads.size()<3) {
				Optional<Integer> r = null;
				synchronized(pendingTest){
					if (!pendingTest.isEmpty()) r = Optional.of(pendingTest.remove(0));
				}
				
				if(r!=null) {
					ThreadScript s = new ThreadScript(r.orElse(-1), conn);
					threads.add(s);
					s.start();
				}
			}
			for(i=0;i<threads.size();i++) {
				if(!threads.isEmpty() && threads.get(i).status==Status.failed) {
					int testid = threads.get(i).testid;
					log.info("Attempt of data processing test: "+testid
							+" ended with error, the status of the test now results as Failed");
					
					threads.remove(i);
					try {
						tests.setFailed(testid);
					} catch (TestNotFoundException | DBException | TestNotCompletedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(!threads.isEmpty() && threads.get(i).status==Status.completed) {
					try {
						saveProcessedResult(threads.get(i).testid, threads.get(i).result);
					} catch (TestNotFoundException | DBException | TestNotCompletedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					threads.remove(i);
				}
			}
		
	}
		
	

	/**
	 * @param testid
	 * @param result
	 * @throws TestNotFoundException
	 * @throws DBException
	 * @throws TestNotCompletedException
	 */
	public void saveProcessedResult(int testid, String result) throws TestNotFoundException,
		DBException, TestNotCompletedException {
		
		try {
			Test t = tests.getTest(testid);
			PublicKey pubkey = genKey(t.getPublicKey().get(0), t.getPublicKey().get(1));
        	String encryptedString = Base64.getEncoder().encodeToString(encrypt(result.getBytes(), pubkey));
			tests.setResult(testid, encryptedString);
			whiteboard.sendInfo(testid);
		} catch (NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
			System.err.println(e.getMessage());
		}

	}
	
	 
	
	/**
	 * @param modulus
	 * @param exponent
	 * @return
	 */
	private PublicKey genKey(String modulus, String exponent) {
	    	PublicKey pub = null;
	    	try {                    
	            RSAPublicKeySpec spec = new RSAPublicKeySpec(new BigInteger(modulus), new BigInteger(exponent));
	            KeyFactory factory = KeyFactory.getInstance("RSA");
	            pub = factory.generatePublic(spec);
	    	}                       
	        catch( Exception e ) {
	            System.out.println(e.toString());       
	        }        
	    	return pub;
	}
	    	
	 
	/**
	 * @param data
	 * @param publicKey
	 * @return
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws InvalidKeyException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 */
	private byte[] encrypt(byte[] data, PublicKey publicKey) throws BadPaddingException, IllegalBlockSizeException,
	 	InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
	    
		 	Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
	        return cipher.doFinal(data);
	}

}






enum Status {
    working,
    failed,
    completed
}




class ThreadScript extends Thread{
	
	private IDbConnection conn;
	public int testid;
	public String result;
	Status status;
	
	/**
	 * @param testid
	 * @param conn
	 */
	public ThreadScript(int testid, IDbConnection conn) {
		this.testid = testid;
		this.status = Status.working;
		this.conn = conn;
	}
	
	public void run() {
		String s = null;
		result = null;
		String strLevel = "Parkinson-Disease-Level-";
		List<Integer> results = new ArrayList<Integer>();
		int average = 0;
		try {	            
			String changeStorage = conn.getVenvScriptPath().equals(conn.getFilePath())? "" : "&& "+
					conn.getVenvScriptPath().charAt(0)+": ";
			
			Process p = Runtime.getRuntime().exec("cmd /c "+conn.getFilePath().charAt(0)+": "
					+ changeStorage
					+ "&& cd "+conn.getVenvScriptPath()
					+ "&& activate && cd "+conn.getFilePath()
					+ "&& python predict.py --src_dir raw-test-data\\test"+testid);
			
			BufferedReader stdInput = new BufferedReader(new 
		         InputStreamReader(p.getInputStream()));

		    BufferedReader stdError = new BufferedReader(new 
		         InputStreamReader(p.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
            	results.add(Integer.parseInt(s.charAt(s.lastIndexOf(strLevel)+strLevel.length())+""));
            }
            if(results.isEmpty()) {
            	status=Status.failed; 
            	return;
            }
            for(int c : results) average = average + c;
            result = Long.toString(Math.round((double)average/results.size()));
            status = Status.completed;
            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
            stdInput.close();
            stdError.close();
            return;
        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            status=Status.failed; 
            return;
        }		
	}
}





