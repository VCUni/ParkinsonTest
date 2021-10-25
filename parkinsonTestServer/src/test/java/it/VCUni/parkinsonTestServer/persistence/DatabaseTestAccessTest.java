package it.VCUni.parkinsonTestServer.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import it.VCUni.parkinsonTestServer.TestSpringContainer;
import it.VCUni.parkinsonTestServer.entity.User;
import it.VCUni.parkinsonTestServer.exception.DBException;
import it.VCUni.parkinsonTestServer.exception.MultipleTestException;
import it.VCUni.parkinsonTestServer.exception.TestNotCompletedException;
import it.VCUni.parkinsonTestServer.exception.TestNotFoundException;
import it.VCUni.parkinsonTestServer.exception.UserAlreadyExistsException;
import it.VCUni.parkinsonTestServer.exception.UserNotFoundException;
import it.VCUni.parkinsonTestServer.persistence.samples.DatabaseSampleAccess;
import it.VCUni.parkinsonTestServer.persistence.tests.DatabaseTestAccess;
import it.VCUni.parkinsonTestServer.persistence.users.DatabaseUserAccess;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = TestSpringContainer.class)
public class DatabaseTestAccessTest {
	
	@Autowired
	DatabaseTestAccess testdb;
	
	@Autowired
	DatabaseUserAccess userdb;
	
	@Autowired
	DatabaseSampleAccess sampledb;
	
	public void initSample() throws DBException {
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
	
	/**
	 * @throws UserAlreadyExistsException
	 * @throws DBException
	 * @throws UserNotFoundException
	 * @throws MultipleTestException
	 * @throws TestNotFoundException
	 * @throws TestNotCompletedException
	 * @throws SQLException
	 */
	@Test
	public void createTest() throws UserAlreadyExistsException, DBException, UserNotFoundException,
		MultipleTestException, TestNotFoundException, TestNotCompletedException, SQLException {
		
		initSample();
		userdb.registerUser(new User("gianni", "lorenzo", "GNNLRZ", "12/05/2020", "Test"), "default");
		User u = userdb.getUser("GNNLRZ");
		it.VCUni.parkinsonTestServer.entity.Test t = testdb.generateTest(u.getCf(), "mod", "exp");
		
		assertThrows(UserNotFoundException.class, () -> 
			testdb.generateTest("fake", "mod", "exp")
		);
		
		assertEquals(testdb.getTest(t.getId()), t);
		
		assertEquals("GNNLRZ",t.getUser());

		assertThrows(TestNotFoundException.class, () -> 
			testdb.getTest(0)
		);
		
		testdb.saveAudio("GNNLRZ", "fake/url", t.getId());
		
		assertThrows(TestNotCompletedException.class, () -> 
			testdb.setFailed(t.getId())
		);
		
		assertThrows(TestNotCompletedException.class, () -> 
			testdb.setPending(t.getId())
		);
		testdb.saveAudio("GNNLRZ", "fake/url", t.getId());
		testdb.saveAudio("GNNLRZ", "fake/url", t.getId());
		testdb.saveAudio("GNNLRZ", "fake/url", t.getId());
		
		testdb.setPending(t.getId());
		assertEquals(testdb.getPendingTests().size(),1);
		testdb.setResult(t.getId(), "2");
	}
	
	
	/**
	 * @throws DBException
	 * @throws UserAlreadyExistsException
	 * @throws UserNotFoundException
	 * @throws MultipleTestException
	 */
	@Test
	public void genMultiTest() throws DBException, UserAlreadyExistsException, UserNotFoundException, MultipleTestException {
		initSample();
		userdb.registerUser(new User("gianni", "lorenzo", "GNNLRZ", "12/05/2020", "Test"), "default");
		User u = userdb.getUser("GNNLRZ");
		@SuppressWarnings("unused")
		it.VCUni.parkinsonTestServer.entity.Test t = testdb.generateTest(u.getCf(), "mod", "exp");
		
		assertThrows(MultipleTestException.class, () -> 
			testdb.generateTest(u.getCf(), "mod", "exp")
		);
	}

	
	/**
	 * @throws DBException
	 * @throws UserAlreadyExistsException
	 * @throws UserNotFoundException
	 * @throws MultipleTestException
	 * @throws TestNotFoundException
	 * @throws TestNotCompletedException
	 */
	@Test
	public void currentTest() throws DBException, UserAlreadyExistsException, UserNotFoundException,
		MultipleTestException, TestNotFoundException, TestNotCompletedException {
		
		initSample();
		userdb.registerUser(new User("gianni", "lorenzo", "GNNLRZ", "12/05/2020", "Test"), "default");
		User u = userdb.getUser("GNNLRZ");
		it.VCUni.parkinsonTestServer.entity.Test t = testdb.generateTest(u.getCf(), "mod", "exp");
		
		assertEquals(userdb.getCurrentTest(u.getCf()),t.getId());
		
		assertThrows(TestNotFoundException.class, () -> 
			testdb.setResult(123, "0")
		);
		
		assertThrows(TestNotCompletedException.class, () -> 
			testdb.setResult(t.getId(), "0")
		);
		
		
		assertEquals(userdb.getCurrentTest(u.getCf()),1);
	}
	
	
	@Autowired
	DbTester tests;
	
	@BeforeEach
	public void droptable() throws Exception {
		tests.drop();
	}

}
