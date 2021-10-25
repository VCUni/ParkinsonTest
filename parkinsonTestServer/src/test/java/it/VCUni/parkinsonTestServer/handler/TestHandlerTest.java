package it.VCUni.parkinsonTestServer.handler;

import static org.junit.jupiter.api.Assertions.assertThrows;

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
import it.VCUni.parkinsonTestServer.exception.TestNotFoundException;
import it.VCUni.parkinsonTestServer.exception.UserAlreadyExistsException;
import it.VCUni.parkinsonTestServer.exception.UserNotFoundException;
import it.VCUni.parkinsonTestServer.persistence.DbTester;
import it.VCUni.parkinsonTestServer.handler.TestHandler;
import it.VCUni.parkinsonTestServer.handler.UserAuthHandler;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = TestSpringContainer.class)
public class TestHandlerTest {

	@Autowired
	UserAuthHandler handler;
		
	@Autowired
	TestHandler testhandler;
	
	/**
	 * @throws MultipleTestException
	 * @throws UserAlreadyExistsException
	 * @throws DBException
	 * @throws UserNotFoundException
	 * @throws TestNotFoundException
	 */
	@Test
	public void testoperations() throws MultipleTestException, UserAlreadyExistsException, DBException,
		UserNotFoundException, TestNotFoundException {
		
		handler.registerUser(new User("Giovanna", "Auditore", "GVNDTR", "10/12/2000", "Train"), "pass");
		
		assertThrows(UserNotFoundException.class, () -> 
			testhandler.createTest("G", "mod", "exp")
		);
		
		assertThrows(TestNotFoundException.class, () -> 
			testhandler.saveAudio("GVNDTR", "fake/url", 0)
		);
	
	}
		
		
	@Autowired
	DbTester test;
	
	@BeforeEach
	public void drop() throws Exception {
		test.drop();
	}
}
