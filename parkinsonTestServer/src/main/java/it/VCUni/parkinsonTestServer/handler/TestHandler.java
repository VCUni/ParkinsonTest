package it.VCUni.parkinsonTestServer.handler;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.VCUni.parkinsonTestServer.entity.Test;
import it.VCUni.parkinsonTestServer.exception.DBException;
import it.VCUni.parkinsonTestServer.exception.MultipleTestException;
import it.VCUni.parkinsonTestServer.exception.TestNotCompletedException;
import it.VCUni.parkinsonTestServer.exception.TestNotFoundException;
import it.VCUni.parkinsonTestServer.exception.UserNotFoundException;
import it.VCUni.parkinsonTestServer.interfaces.TestAccess;
import it.VCUni.parkinsonTestServer.settings.IDbConnection;

/**
 * Handler for test's management
*/
@Component
public class TestHandler {
	
	@Autowired
	TestAccess tests;
	
	@Autowired
	Logger logger;
	
	@Autowired
	ThreadHandler threadhandler;
	
	@Autowired
	IDbConnection conn;
	

	/**
	 * @param cf
	 * @param publicKeyMod
	 * @param publicKeyExp
	 * @return
	 * @throws UserNotFoundException
	 * @throws DBException
	 * @throws MultipleTestException
	 */
	public Test createTest(String cf, String publicKeyMod, String publicKeyExp) throws
	UserNotFoundException, DBException, MultipleTestException {
		
		logger.info("nuovo test creato su richiesta di " + cf);
		return tests.generateTest(cf, publicKeyMod, publicKeyExp);
	}
	

	/**
	 * @param userlogin
	 * @param url
	 * @param testid
	 * @throws UserNotFoundException
	 * @throws DBException
	 * @throws TestNotFoundException
	 */
	public void saveAudio(String userlogin, String url, int testid) throws UserNotFoundException, 
		DBException, TestNotFoundException {

		tests.saveAudio(userlogin, url, testid);
	}
	
	
	/**
	 * @param testid
	 * @return
	 * @throws DBException
	 * @throws TestNotFoundException
	 */
	public Test getTest(int testid) throws DBException, TestNotFoundException {
		return tests.getTest(testid);
	}
	
	/**
	 * @param testid
	 * @param result
	 * @throws TestNotFoundException
	 * @throws DBException
	 * @throws TestNotCompletedException
	 */
	public void setResult(int testid, String result) throws TestNotFoundException, DBException, TestNotCompletedException {
		tests.setResult(testid, result);
	}
	
	/**
	 * @param testid
	 * @throws TestNotFoundException
	 * @throws DBException
	 * @throws TestNotCompletedException
	 */
	public void setPending(int testid) throws TestNotFoundException, DBException, TestNotCompletedException {
		tests.setPending(testid);
		threadhandler.pendingTest.add(testid);
	}	
}
