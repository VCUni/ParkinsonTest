package it.VCUni.parkinsonTestServer.interfaces;

import java.sql.SQLException;
import java.util.List;

import it.VCUni.parkinsonTestServer.entity.Test;
import it.VCUni.parkinsonTestServer.entity.UploadStatus;
import it.VCUni.parkinsonTestServer.exception.DBException;
import it.VCUni.parkinsonTestServer.exception.MultipleTestException;
import it.VCUni.parkinsonTestServer.exception.TestNotCompletedException;
import it.VCUni.parkinsonTestServer.exception.TestNotFoundException;
import it.VCUni.parkinsonTestServer.exception.UserNotFoundException;

/**
 *  Test's database access interface
*/
public interface TestAccess {
	
	Test getTest(int testid) throws DBException, TestNotFoundException;

	Test generateTest(String cf, String publicKeyMod, String publicKeyExp) throws UserNotFoundException, 
		DBException, MultipleTestException;

	void setPending(int testid) throws TestNotFoundException, DBException, TestNotCompletedException;
	
	String savePath(String userlogin, String path, int testid) 
			throws UserNotFoundException, DBException, TestNotFoundException;
	
	void setResult(int testid, String result) throws TestNotFoundException, DBException, TestNotCompletedException;

	List<Integer> getPendingTests() throws SQLException;

	void setFailed(int testid) throws TestNotFoundException, DBException, TestNotCompletedException;

	void setCompleted(int testid) throws TestNotFoundException, DBException, TestNotCompletedException;

	void deletePath(int testid, String path) throws TestNotFoundException, DBException;

	int setUploadStatus(int testid, UploadStatus newstate, UploadStatus expected) throws DBException;
		
}
