package it.VCUni.parkinsonTestServer.service;

import java.sql.SQLException;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;

import it.VCUni.parkinsonTestServer.entity.User;
import it.VCUni.parkinsonTestServer.exception.DBException;
import it.VCUni.parkinsonTestServer.exception.MultipleTestException;
import it.VCUni.parkinsonTestServer.exception.UserNotFoundException;
import it.VCUni.parkinsonTestServer.exception.WrongCredentialsException;
import it.VCUni.parkinsonTestServer.handler.DocumentHandler;
import it.VCUni.parkinsonTestServer.handler.UserHandler;

/**
 *  Service that allows to login and modify the user's info
*/
@Path("user")
@RestController
@Produces("application/json")
public class UserService {

	@Autowired
	DocumentHandler documenthandler;
	
	@Autowired
	UserHandler userhandler;
	
	@Autowired
	Logger log;
	
	
	/**
	 * @param oldpass
	 * @param newpass
	 * @return
	 */
	@POST
	@Path("changepassword")
	public Response changePassword(@FormParam("oldpass") String oldpass, @FormParam("newpass") String newpass) {
		try {
			userhandler.changePassword(SecurityContextHolder.getContext().getAuthentication().getName(), oldpass, newpass);
		} catch (DBException | UserNotFoundException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}catch (WrongCredentialsException e) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		return Response.status(Status.OK).build();
	}
	
	
	/**
	 * @return
	 * @throws UserNotFoundException
	 * @throws SQLException
	 * @throws DBException
	 */
	@GET
	@Path("account")
	public User getAccount() throws UserNotFoundException, SQLException, DBException  {
		return userhandler.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
	}
	
	
	/**
	 * @return
	 * @throws UserNotFoundException
	 * @throws DBException
	 * @throws MultipleTestException
	 */
	@GET
	@Path("current")
	public Response getCurrent() throws UserNotFoundException, DBException, MultipleTestException {
		try {

			return Response.ok(userhandler.getCurrentTest(SecurityContextHolder.getContext().getAuthentication().getName())).build();
		} catch(UserNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		} catch(MultipleTestException e) {
			return Response.status(Status.CONFLICT).build();
		} catch(DBException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	
	@Autowired
	TokenGenerator generator;
	
	/**
	 *  Renew the deadline without login, generate new token
	*/
	/**
	 * @return
	 * @throws UserNotFoundException
	 * @throws Exception
	 */
	@POST
	@Path("renew")
	@Produces("text/plain")
	public Response renew() throws UserNotFoundException, Exception {
		String user = SecurityContextHolder.getContext().getAuthentication().getName();
		return Response.ok(generator.generateToken(user)).build();
	}
}
