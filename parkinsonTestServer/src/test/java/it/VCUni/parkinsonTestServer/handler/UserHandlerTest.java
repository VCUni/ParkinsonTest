package it.VCUni.parkinsonTestServer.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import it.VCUni.parkinsonTestServer.TestSpringContainer;
import it.VCUni.parkinsonTestServer.entity.User;
import it.VCUni.parkinsonTestServer.exception.DBException;
import it.VCUni.parkinsonTestServer.exception.IncorrectUrlException;
import it.VCUni.parkinsonTestServer.exception.MultipleTestException;
import it.VCUni.parkinsonTestServer.exception.UserAlreadyExistsException;
import it.VCUni.parkinsonTestServer.exception.UserNotFoundException;
import it.VCUni.parkinsonTestServer.exception.WrongCredentialsException;
import it.VCUni.parkinsonTestServer.handler.UserAuthHandler;
import it.VCUni.parkinsonTestServer.handler.UserHandler;
import it.VCUni.parkinsonTestServer.persistence.DbTester;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes=TestSpringContainer.class)
public class UserHandlerTest {

	@Autowired
	UserHandler userhandler;
	
	@Autowired
	UserAuthHandler authhandler;
	
	@Autowired
	DocumentHandler dochandler;
	
	/**
	 * @throws UserAlreadyExistsException
	 * @throws DBException
	 * @throws UserNotFoundException
	 * @throws IncorrectUrlException
	 * @throws WrongCredentialsException
	 * @throws MultipleTestException
	 */
	@Test
	public void useroperations() throws UserAlreadyExistsException, DBException, UserNotFoundException, 
		IncorrectUrlException, WrongCredentialsException, MultipleTestException {
		
		User u = new User("name", "surname", "GCMRSS", "10/10/2010", "Test");
	
		authhandler.registerUser(u, "password");
		
		userhandler.changePassword(u.getCf(), "password", "new");
		
		userhandler.getUser("GCMRSS");
		
		userhandler.getCurrentTest("GCMRSS");
		
		userhandler.getUserResults("GCMRSS");
	}
	
	
	@Autowired
	DbTester test;
	
	@BeforeEach
	public void drop() throws Exception {
		test.drop();
	}
}
