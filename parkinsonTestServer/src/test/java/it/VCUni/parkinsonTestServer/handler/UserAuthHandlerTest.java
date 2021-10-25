package it.VCUni.parkinsonTestServer.handler;

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
import it.VCUni.parkinsonTestServer.exception.UserAlreadyExistsException;
import it.VCUni.parkinsonTestServer.exception.UserNotFoundException;
import it.VCUni.parkinsonTestServer.handler.UserAuthHandler;
import it.VCUni.parkinsonTestServer.handler.UserHandler;
import it.VCUni.parkinsonTestServer.persistence.DbTester;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = TestSpringContainer.class)
public class UserAuthHandlerTest {
	
	@Autowired
	UserAuthHandler handler;
	
	@Autowired
	UserHandler userhandler;
	
	/**
	 * @throws UserAlreadyExistsException
	 * @throws DBException
	 * @throws UserNotFoundException
	 */
	@Test
	public void register() throws UserAlreadyExistsException, DBException, UserNotFoundException {
		handler.registerUser(new User("Giovanna", "Auditore", "GVNDTR", "10/12/2000", "Train"), "pass");
		assertEquals(userhandler.getUser("GVNDTR").getCf(),"GVNDTR");
	}
	
	
	@Autowired
	DbTester test;
	
	@BeforeEach
	public void drop() throws Exception {
		test.drop();
	}

}
