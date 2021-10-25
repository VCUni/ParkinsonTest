package it.VCUni.parkinsonTestServer.handler;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.VCUni.parkinsonTestServer.entity.User;
import it.VCUni.parkinsonTestServer.exception.DBException;
import it.VCUni.parkinsonTestServer.exception.MultipleTestException;
import it.VCUni.parkinsonTestServer.exception.UserNotFoundException;
import it.VCUni.parkinsonTestServer.exception.WrongCredentialsException;
import it.VCUni.parkinsonTestServer.interfaces.UserAccess;

/**
 *  Handler for information management and user's authorization
*/
@Component
public class UserHandler {
	
	@Autowired
	UserAccess users;

	@Autowired
	Logger log;
	
	
	/**
	 * @param email
	 * @param oldpass
	 * @param newpass
	 * @throws DBException
	 * @throws UserNotFoundException
	 * @throws WrongCredentialsException
	 */
	public void changePassword(String email, String oldpass, String newpass) throws DBException, 
		UserNotFoundException, WrongCredentialsException {
		
		log.info("richiesta cambio password dell'utente " + email);
		users.changePassword(email, oldpass, newpass);
	}
	
	
	/**
	 * @param userlogin
	 * @return
	 * @throws UserNotFoundException
	 * @throws DBException
	 */
	public User getUser(String userlogin) throws UserNotFoundException, DBException {
		
		return users.getUser(userlogin);
	}
	
	
	/**
	 * @param userlogin
	 * @return
	 * @throws UserNotFoundException
	 * @throws DBException
	 */
	public Stream<String> getUserResults(String userlogin) throws UserNotFoundException, DBException {
		
		return users.getUserResults(userlogin);
	}
	
	/**
	 * @param userlogin
	 * @return
	 * @throws UserNotFoundException
	 * @throws DBException
	 * @throws MultipleTestException
	 */
	public Integer getCurrentTest(String userlogin) throws UserNotFoundException, DBException, MultipleTestException {
		
		return users.getCurrentTest(userlogin);
	}
	
}
