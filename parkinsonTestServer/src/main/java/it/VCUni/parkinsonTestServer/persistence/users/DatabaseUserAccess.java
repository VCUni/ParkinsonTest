package it.VCUni.parkinsonTestServer.persistence.users;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.j256.ormlite.stmt.SelectArg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import it.VCUni.parkinsonTestServer.entity.TestStatus;
import it.VCUni.parkinsonTestServer.entity.User;
import it.VCUni.parkinsonTestServer.exception.DBException;
import it.VCUni.parkinsonTestServer.exception.MultipleTestException;
import it.VCUni.parkinsonTestServer.exception.UserAlreadyExistsException;
import it.VCUni.parkinsonTestServer.exception.UserNotFoundException;
import it.VCUni.parkinsonTestServer.exception.WrongCredentialsException;
import it.VCUni.parkinsonTestServer.interfaces.UserAccess;
import it.VCUni.parkinsonTestServer.persistence.AbstractDao;
import it.VCUni.parkinsonTestServer.persistence.ConnectionSource;

/**
 *  DAO Class for user's list, implements UserDetailsService to permit the login with spring security
*/
@Component
public class DatabaseUserAccess extends AbstractDao<Integer, UserDb> implements UserAccess, UserDetailsService {
	
	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	/**
	 * @param source
	 * @throws DBException
	 * @throws IOException
	 */
	@Autowired
	public DatabaseUserAccess(ConnectionSource source) throws DBException, IOException {
		
		super(source, UserDb.class);
	}

	
	@Override
	public UserDetails loadUserByUsername(String userlogin) throws UsernameNotFoundException {
		
		UserDb user;

		try {
			user = getUserDb(userlogin);
		} catch (Exception e) {throw new UsernameNotFoundException("");}

		return org.springframework.security.core.userdetails.User.withUsername(userlogin).password(user.password)
				.roles("user").build();
	}

	
	@Override
	public void registerUser(User user, String password) throws UserAlreadyExistsException, DBException{
		
		if (userExists(user.getCf()))
			throw new UserAlreadyExistsException();

		UserDb u = new UserDb(user);
		u.setPassword(passwordEncoder.encode(password));

		try {
			dao.create(u);
		} catch(SQLException ex) {throw new DBException(ex.toString());}
	}

	
	/**
	 * @param userlogin
	 * @return
	 * @throws UserNotFoundException
	 * @throws DBException
	 */
	public UserDb getUserDb(String userlogin) throws UserNotFoundException, DBException {
		
		try {
			UserDb user = where().eq(UserDb.CF, new SelectArg(userlogin)).queryForFirst();
			if (user == null)
				throw new UserNotFoundException();
			return user;
		} catch(SQLException ex) {throw new DBException(ex.toString());}
	}

	
	/**
	 * @return User
	 */
	@Override
	public User getUser(String userlogin) throws UserNotFoundException, DBException {
		return getUserDb(userlogin).toUser();
	}

	
	/**
	 * @return if the user exists
	 */
	@Override
	public boolean userExists(String userlogin) {
		
		try {
			return where().eq(UserDb.CF, new SelectArg(userlogin)).queryForFirst() != null;
		} catch (SQLException e) {
			return false;
		}
	}

	
	/**
	 * @return hashed password
	 */
	@Override
	public int getHashedPassword(String userlogin) throws UserNotFoundException, DBException {
		
		UserDb user = getUserDb(userlogin);
		return user.getPassword().hashCode();
	}
	
	
	@Override
	public void changePassword(String userlogin, String currentPassword, String newPassword) 
			throws UserNotFoundException, WrongCredentialsException, DBException {
		
		UserDb u = getUserDb(userlogin);
		if (u == null) throw new UserNotFoundException();

		if (passwordEncoder.matches(currentPassword, u.getPassword())) {
			u.setPassword(passwordEncoder.encode(newPassword));
			try {
				dao.update(u);
			} catch(SQLException ex) {throw new DBException(ex.toString());}
		}
		else throw new WrongCredentialsException();
	}
	
			
	@Override
	public Stream<User> getUsers() throws DBException {
			return all().map(UserDb::toUser);
	}
	
	
	
	/**
	 * @return testid
	 */
	@Override
	public Integer getCurrentTest(String userlogin) throws UserNotFoundException, DBException, MultipleTestException {
		
		List<Integer> current = getUserDb(userlogin).tests.stream()
				.filter(x -> x.getStatus()==TestStatus.Uncompleted).map(x -> x.getId()).collect(Collectors.toList());
		
		if(current.size()==0) return 0;
		else if (current.size()>1) throw new MultipleTestException();
		else return current.get(0);
	}
	
}