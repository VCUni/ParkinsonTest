package it.VCUni.parkinsonTestServer.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.VCUni.parkinsonTestServer.persistence.reports.DatabaseReportAccess;
import it.VCUni.parkinsonTestServer.persistence.samples.DatabaseSampleAccess;
import it.VCUni.parkinsonTestServer.persistence.tests.DatabaseTestAccess;
import it.VCUni.parkinsonTestServer.persistence.users.DatabaseUserAccess;

/**
 *  Permits to delete the databases during test
*/
@Component
public class DbTester {
	
	@Autowired
	DatabaseUserAccess userDB;
	@Autowired
	DatabaseTestAccess testDB;
	@Autowired
	DatabaseSampleAccess sampleDB;
	@Autowired
	DatabaseReportAccess reportDB;
	
	public void drop() throws Exception	{
		userDB.dropTable();
		testDB.dropTable();
		sampleDB.dropTable();
		reportDB.dropTable();
		
	}
}
	

