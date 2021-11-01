package it.VCUni.parkinsonTestServer.interfaces;

import java.util.stream.Stream;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import it.VCUni.parkinsonTestServer.entity.User;
import it.VCUni.parkinsonTestServer.exception.DBException;
import it.VCUni.parkinsonTestServer.exception.MultipleTestException;
import it.VCUni.parkinsonTestServer.exception.UserAlreadyExistsException;
import it.VCUni.parkinsonTestServer.exception.UserNotFoundException;
import it.VCUni.parkinsonTestServer.exception.WrongCredentialsException;

/**
 *  User's database access interface
*/
public interface UserAccess {
	void registerUser(User user, String password) throws UserAlreadyExistsException, DBException;
	
	User getUser(String userlogin) throws UserNotFoundException, DBException;
	
	boolean userExists(String userlogin);
	
	int getHashedPassword(String userlogin) throws UserNotFoundException, DBException;
	
	void changePassword(String userlogin, String currentPassword, String newPassword)
			throws UserNotFoundException, WrongCredentialsException, DBException;
	
	Stream<User> getUsers() throws DBException;
	
	UserDetails loadUserByUsername(String userlogin) throws UsernameNotFoundException;

	Integer getCurrentTest(String userlogin) throws UserNotFoundException, DBException, MultipleTestException;

}