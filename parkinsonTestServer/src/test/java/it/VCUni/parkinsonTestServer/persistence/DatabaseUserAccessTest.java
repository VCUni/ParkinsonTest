package it.VCUni.parkinsonTestServer.persistence;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
import it.VCUni.parkinsonTestServer.persistence.users.DatabaseUserAccess;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes=TestSpringContainer.class)
public class DatabaseUserAccessTest {
	
	@Autowired
	DatabaseUserAccess users;
	
	
	/**
	 * @throws UserAlreadyExistsException
	 * @throws DBException
	 */
	@Test
	public void registerUser() throws UserAlreadyExistsException, DBException  {
		users.registerUser(new User("gianni", "lorenzo", "cf", "12/05/2020", "Test"), "default");
		assertTrue(users.userExists("cf"));
		
		assertThrows(UserAlreadyExistsException.class, () ->{
			users.registerUser(new User("gianni", "lorenzo", "cf", "12/05/2000", "Train"), "default");
		});
		
		users.registerUser(new User("gianni", "lorenzo", "cf1", "06/11/1990", "Train"), "default");
	}
	
	
	/**
	 * @throws UserNotFoundException
	 */
	@Test
	public void userNotExist() throws UserNotFoundException{
		assertThrows(UserNotFoundException.class, () ->{users.getUser("cf");});
	}
	
	
	/**
	 * @throws UserAlreadyExistsException
	 * @throws DBException
	 */
	@Test
	public void duplicateUser() throws UserAlreadyExistsException, DBException {
		users.registerUser(new User("giacomo", "rossi", "GCMRSS", "10/03/1971", "Test"), "password");
		assertThrows(UserAlreadyExistsException.class, () ->{
			users.registerUser(new User("giacomo", "rossi", "GCMRSS", "10/03/1971", "Test"), "password");
		});
	}
	
	
	/**
	 * @throws UserAlreadyExistsException
	 * @throws DBException
	 * @throws UserNotFoundException
	 * @throws IncorrectUrlException
	 * @throws WrongCredentialsException
	 */
	@Test
	public void userChangePassword() throws UserAlreadyExistsException, DBException, UserNotFoundException, 
		IncorrectUrlException, WrongCredentialsException {
		
		User user = new User("giacomo", "rossi", "GCMRSS", "10/03/1971", "Test");
		users.registerUser(user, "092454");
		users.changePassword("GCMRSS", "092454", "nuova");
		
		assertThrows(WrongCredentialsException.class, () -> {
			users.changePassword("GCMRSS", "092454", "nuova");
		});
		
		assertThrows(UserNotFoundException.class, () -> {
			users.changePassword("GCM", "092454", "nuova");
		});		
	}
	
	
	/**
	 * @throws UserAlreadyExistsException
	 * @throws DBException
	 * @throws UserNotFoundException
	 * @throws IncorrectUrlException
	 * @throws WrongCredentialsException
	 */
	@Test
	public void userResults() throws UserAlreadyExistsException, DBException, UserNotFoundException, 
		IncorrectUrlException, WrongCredentialsException {
		
		User user = new User("giacomo", "rossi", "GCMRSS", "10/03/1971", "Test");
		users.registerUser(user, "092454");
		
		users.getUserResults("GCMRSS");		
	}
	
	
	/**
	 * @throws UserAlreadyExistsException
	 * @throws DBException
	 * @throws UserNotFoundException
	 * @throws IncorrectUrlException
	 * @throws WrongCredentialsException
	 * @throws MultipleTestException
	 */
	@Test
	public void userCurrent() throws UserAlreadyExistsException, DBException, UserNotFoundException, 
		IncorrectUrlException, WrongCredentialsException, MultipleTestException {
		
		User user = new User("giacomo", "rossi", "GCMRSS", "10/03/1971", "Test");
		users.registerUser(user, "092454");
		
		assertEquals(users.getCurrentTest("GCMRSS"),0);
				
	}
	
	
	@Autowired
	DbTester tests;
	
	@BeforeEach
	public void droptable() throws Exception {
		tests.drop();
	}
}
