package it.VCUni.parkinsonTestServer.persistence.tests;

import java.io.IOException;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.VCUni.parkinsonTestServer.entity.Test;
import it.VCUni.parkinsonTestServer.entity.TestStatus;
import it.VCUni.parkinsonTestServer.entity.UploadStatus;
import it.VCUni.parkinsonTestServer.exception.DBException;
import it.VCUni.parkinsonTestServer.exception.MultipleTestException;
import it.VCUni.parkinsonTestServer.exception.TestNotCompletedException;
import it.VCUni.parkinsonTestServer.exception.TestNotFoundException;
import it.VCUni.parkinsonTestServer.exception.UserNotFoundException;
import it.VCUni.parkinsonTestServer.persistence.AbstractDao;
import it.VCUni.parkinsonTestServer.persistence.ConnectionSource;
import it.VCUni.parkinsonTestServer.persistence.samples.DatabaseSampleAccess;
import it.VCUni.parkinsonTestServer.interfaces.TestAccess;
import it.VCUni.parkinsonTestServer.persistence.users.DatabaseUserAccess;
import it.VCUni.parkinsonTestServer.persistence.users.UserDb;

/**
 *  DAO Class for user's tests
*/
@Component
public class DatabaseTestAccess extends AbstractDao<Integer, TestDb> implements TestAccess{
	
	@Autowired
	DatabaseUserAccess userDb;
	
	@Autowired
	DatabaseSampleAccess sampleDb;
	
	
	public DatabaseTestAccess(ConnectionSource s) throws IOException, Exception {
		super(s, TestDb.class);
	}
	
	
	/**
	 * @return Test
	 */
	@Override
	public Test generateTest(String cf, String publicKeyMod , String publicKeyExp) 
			throws UserNotFoundException, DBException, MultipleTestException {
		
		UserDb u = userDb.getUserDb(cf);
		if(u == null) throw new UserNotFoundException();
		if(userDb.getCurrentTest(u.getCf()) != 0) throw new MultipleTestException();
		TestDb test = new TestDb();
		
		test.uploadstatus=UploadStatus.Free;
		test.setTrainResult(null);
		test.user = u;
		test.setStatus(TestStatus.Uncompleted);
		if(publicKeyMod.equals("") || publicKeyExp.equals("")) {
			test.setPubMod(null);
			test.setPubExp(null);
		}
		else {
			test.setPubMod(publicKeyMod);
			test.setPubExp(publicKeyExp);
		}
		List<String> str= sampleDb.randSample();
		test.sample1 = str.get(0);
		test.sample2 = str.get(1);
		test.sample3 = str.get(2);
		test.sample4 = str.get(3);
		test.url1 = null;
		test.url2 = null;
		test.url3 = null;
		test.url4 = null;
		
		try {
			dao.create(test);
		} catch (SQLException ex) {
			throw new DBException(ex.toString());
		}
		return test.toTest();
	}
	
	
	/**
	 * @param testid
	 * @return
	 * @throws TestNotFoundException
	 * @throws DBException
	 */
	public TestDb getTestDb(int testid) throws TestNotFoundException, DBException {
		try {
			TestDb test = where().eq(TestDb.ID, new SelectArg(testid)).queryForFirst();
			if (test == null)
				throw new TestNotFoundException();
			return test;
		} catch(SQLException ex) {throw new DBException(ex.toString());}
	}
	
	
	/**
	 * @return Test
	 */
	@Override
	public Test getTest(int testid) throws DBException, TestNotFoundException {
		return getTestDb(testid).toTest();
	}
	
	
	/**
	 * @return list of testid
	 */
	@Override
	public List<Integer> getPendingTests() throws SQLException {
		return where().eq(TestDb.STATUS, new SelectArg(TestStatus.Pending)).query().stream()
				.map(element -> element.id).collect(Collectors.toList());
	}
	
	
	@Override
	public String savePath(String userlogin, String path, int testid) 
			throws UserNotFoundException, DBException, TestNotFoundException {
		
		TestDb test = getTestDb(testid);
		UserDb u = userDb.getUserDb(userlogin);
		if (u == null) throw new UserNotFoundException();

		Test t = getTest(testid);
		String topsample = t.getSampleList().get(0);
		String samplepath = path+topsample+".wav";
		
		if(topsample.equals(test.sample1)) test.url1 = samplepath;
		else if(topsample.equals(test.sample2)) test.url2 = samplepath;
		else if(topsample.equals(test.sample3)) test.url3 = samplepath;
		else if(topsample.equals(test.sample4)) test.url4 = samplepath;
		
		try {
			dao.update(test);
		} catch(SQLException ex) {throw new DBException(ex.toString());}
		return samplepath;
	}
	
	
	@Override
	public void deletePath(int testid, String path) throws TestNotFoundException, DBException {
		
		TestDb test = getTestDb(testid);
		
		if(path.equals(test.url1)) test.url1 = null;
		else if(path.equals(test.url2)) test.url2 = null;
		else if(path.equals(test.url3)) test.url3 = null;
		else if(path.equals(test.url4)) test.url4 = null;
		
		try {
			dao.update(test);
		} catch(SQLException ex) {throw new DBException(ex.toString());}
		return;
	}

	
	@Override
	public void setResult(int testid, String result) throws TestNotFoundException, DBException, TestNotCompletedException {
		TestDb testdb = getTestDb(testid);
		if(getTest(testid).getSampleList().size() != 0) throw new TestNotCompletedException();
		testdb.setTrainResult(result);
		testdb.setStatus(TestStatus.Completed);
		
		try {
			dao.update(testdb);
		} catch(SQLException ex) {throw new DBException(ex.toString());}
		return;
	}
	
	
	@Override
	public void setCompleted(int testid) throws TestNotFoundException, DBException, TestNotCompletedException {
		TestDb testdb = getTestDb(testid);
		if(getTest(testid).getSampleList().size() != 0) throw new TestNotCompletedException();
		testdb.setStatus(TestStatus.Completed);
		
		try {
			dao.update(testdb);
		} catch(SQLException ex) {throw new DBException(ex.toString());}
		return;
	}
	
	
	@Override
	public void setPending(int testid) throws TestNotFoundException, DBException, TestNotCompletedException {
		TestDb testdb = getTestDb(testid);
		if(getTest(testid).getSampleList().size() != 0) throw new TestNotCompletedException();
		testdb.setStatus(TestStatus.Pending);
		
		try {
			dao.update(testdb);
		} catch(SQLException ex) {throw new DBException(ex.toString());}
		return;
	}
	
	
	@Override
	public void setFailed(int testid) throws TestNotFoundException, DBException, TestNotCompletedException {
		TestDb testdb = getTestDb(testid);
		if(getTest(testid).getSampleList().size() != 0) throw new TestNotCompletedException();
		testdb.setStatus(TestStatus.Failed);
		
		try {
			dao.update(testdb);
		} catch(SQLException ex) {throw new DBException(ex.toString());}
		return;
	}
	
	
	@Override
	public int setUploadStatus(int testid, UploadStatus newstate, UploadStatus expected) throws DBException {
		
		try {
			UpdateBuilder<TestDb, Integer> builder = dao.updateBuilder();
		
		
			builder.updateColumnValue(TestDb.UPLOADSTATUS, newstate)
			.where().eq(TestDb.UPLOADSTATUS, expected)
			.and().eq(TestDb.ID, new SelectArg(testid));
		
	
			// L'update fallisce se il test è già soggetto ad un altro upload
			if (builder.update() != 1)
				return 0;
		
		} catch (SQLException ex) {throw new DBException(ex.toString());}
		return 1;
	}
	
}
