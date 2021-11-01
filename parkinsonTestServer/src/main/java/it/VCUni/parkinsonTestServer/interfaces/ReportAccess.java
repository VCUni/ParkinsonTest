package it.VCUni.parkinsonTestServer.interfaces;

import it.VCUni.parkinsonTestServer.exception.DBException;
import it.VCUni.parkinsonTestServer.exception.InvalidPublicKeyException;
import it.VCUni.parkinsonTestServer.exception.ReportNotFoundException;

/**
 *  Report's database access interface
*/
public interface ReportAccess {
	
	void generateReport(String modkey, String expkey, String result) throws InvalidPublicKeyException, DBException;
	
	String getResult(String modkey, String expkey) throws ReportNotFoundException, DBException;
}
