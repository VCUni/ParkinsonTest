package it.VCUni.parkinsonTestServer.service;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import it.VCUni.parkinsonTestServer.entity.User;
import it.VCUni.parkinsonTestServer.exception.DBException;
import it.VCUni.parkinsonTestServer.exception.UserAlreadyExistsException;
import it.VCUni.parkinsonTestServer.handler.UserAuthHandler;

/**
 *  Service that allows to login to the platform
*/
@Path("userauth")
@RestController
@Produces("application/json")
public class UserAuthService {
	
	@Autowired
	AuthenticationManager auth;
	
	@Autowired
	UserAuthHandler authhandler;
	
	@Autowired
	TokenGenerator generator;
	
	@Autowired
	Logger log;
	
	
	/**
	 * @param user
	 * @param password
	 * @return
	 */
	@POST
	@Path("login")
	@Produces("text/plain")
	public Response login(@FormParam("user") String user, @FormParam("password") String password)
	{
		try {
			Authentication authentication = auth.authenticate(new UsernamePasswordAuthenticationToken(user, password));
			
			if (authentication.isAuthenticated())
			{
				log.info("Autenticazione " + user +" riuscita");
				SecurityContextHolder.getContext().setAuthentication(authentication);
				return Response.ok(generator.generateToken(user)).build();
			}
		}
		catch (AuthenticationException ex) {
			log.info("Autenticazione " + user + " fallita " + ex.toString());
		} catch (Exception ex) {
			log.info("Autenticazione " + user + " fallita " + ex.toString());
		}

		return Response.status(Status.UNAUTHORIZED).build();
	}
	

	/**
	 * @param dateBirth
	 * @param password
	 * @param name
	 * @param surname
	 * @param userlogin
	 * @param role
	 * @return
	 */
	@POST
	@Path("register")
	public Response registerUser(
		@FormParam("dateBirth") String dateBirth, @FormParam("password") String password,
		@FormParam("name") String name, @FormParam("surname") String surname, @FormParam("userlogin") String userlogin,
		@FormParam("role") String role)
	{
		try {
			authhandler.registerUser(new User(name, surname, userlogin, dateBirth, role), password);
			return Response.ok().build();
		} catch (UserAlreadyExistsException e) {
			return Response.status(Status.CONFLICT).build();
		} catch (DBException e) {
			log.error(e.toString());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
}
