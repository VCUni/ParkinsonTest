package it.VCUni.parkinsonTestServer.handler;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.VCUni.parkinsonTestServer.entity.User;
import it.VCUni.parkinsonTestServer.exception.DBException;
import it.VCUni.parkinsonTestServer.exception.UserAlreadyExistsException;
import it.VCUni.parkinsonTestServer.interfaces.UserAccess;

/**
 *  Handler that implements registration logic for the users.
*/
@Component
public class UserAuthHandler {

	@Autowired
	Logger log;
	
	@Autowired
	UserAccess users;
	
	/**
	 * @param account
	 * @param password
	 * @throws UserAlreadyExistsException
	 * @throws DBException
	 */
	public void registerUser(User account, String password) throws UserAlreadyExistsException, DBException {
		log.info("Registrazione utente in corso " + account);
		users.registerUser(account, password);
	}
}
