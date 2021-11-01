package it.VCUni.parkinsonTestServer.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;

import it.VCUni.parkinsonTestServer.entity.Test;
import it.VCUni.parkinsonTestServer.entity.TestStatus;
import it.VCUni.parkinsonTestServer.entity.User;
import it.VCUni.parkinsonTestServer.exception.DBException;
import it.VCUni.parkinsonTestServer.exception.MultipleTestException;
import it.VCUni.parkinsonTestServer.exception.ReportNotFoundException;
import it.VCUni.parkinsonTestServer.exception.TestNotCompletedException;
import it.VCUni.parkinsonTestServer.exception.TestNotFoundException;
import it.VCUni.parkinsonTestServer.exception.UserNotFoundException;
import it.VCUni.parkinsonTestServer.handler.TestHandler;
import it.VCUni.parkinsonTestServer.handler.UserHandler;
import it.VCUni.parkinsonTestServer.handler.WhiteBoardHandler;
import it.VCUni.parkinsonTestServer.handler.DocumentHandler;
import it.VCUni.parkinsonTestServer.handler.ReportHandler;

/**
 *  Service that allows to manage test
*/
@Path("/test")
@RestController
@Produces("application/json")
public class TestService {

	@Autowired
	WhiteBoardHandler whiteboard;
	
	@Autowired
	ReportHandler reporthandler;
	
	@Autowired
	TestHandler testhandler;

	@Autowired
	UserHandler userhandler;
	
	@Autowired
	DocumentHandler documenthandler;
	
	@Autowired
	Logger log;
	
	
	/**
	 * @param fileInputStream
	 * @param fileMetaData
	 * @param testid
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("upload/{testid}")
	public Response uploadAudio(@FormDataParam("file") InputStream fileInputStream, 
		@FormDataParam("file") FormDataContentDisposition fileMetaData, @PathParam("testid") int testid) throws IOException {
		   
        boolean tester;
		try {
			if(testhandler.getTest(testid).getStatus()!=TestStatus.Uncompleted) return Response.status(Status.NOT_ACCEPTABLE).build();
			if(userhandler.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getRole().equals("Test")) tester = true;
			else tester = false;
			Test test = testhandler.getTest(testid);
			if(test.getSampleList().size()==0) return Response.status(Status.BAD_REQUEST).build();
			String path = documenthandler.saveDoc(fileInputStream, test.getId(), test.getSampleList().get(0), tester);
			testhandler.saveAudio(SecurityContextHolder.getContext().getAuthentication().getName(), path, testid);
		} catch (UserNotFoundException | TestNotFoundException e) {
			e.printStackTrace();
			return Response.status(Status.NOT_FOUND).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}	
		return Response.status(Status.CREATED).build();
	}
	
	
	/**
	 * @param testid
	 * @return
	 */
	@GET
	@Path("{testid}/processing")
	public Response requestResult(@PathParam("testid") int testid) {
		Test test;
		User us;
		try {
			if(testhandler.getTest(testid).getStatus()!=TestStatus.Uncompleted) return Response.status(Status.NOT_ACCEPTABLE).build();
			us = userhandler.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
			test = testhandler.getTest(testid);
			if(!test.getUser().equals(us.getCf())) return Response.status(Status.EXPECTATION_FAILED).build();
			testhandler.setPending(testid);
		} catch(UserNotFoundException | TestNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		} catch(DBException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} catch(TestNotCompletedException e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		return Response.ok().build();
	}
	
	
	/**
	 * @param testid
	 * @param result
	 * @return
	 */
	@POST
	@Path("{testid}/send/{result}")
	public Response setResult(@PathParam("testid") int testid, @PathParam("result") String result) {
		try {
			if(testhandler.getTest(testid).getStatus()!=TestStatus.Uncompleted) return Response.status(Status.NOT_ACCEPTABLE).build();
			if(!userhandler.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getRole().equals("Train"))
				return Response.status(Status.UNAUTHORIZED).build();
			if(!userhandler.getUser(SecurityContextHolder.getContext().getAuthentication().getName()).getCf().equals(testhandler.getTest(testid).getUser()))
				return Response.status(Status.METHOD_NOT_ALLOWED).build();
			if(Integer.parseInt(result)>5 || Integer.parseInt(result)<0) return Response.status(Status.PRECONDITION_FAILED).build();
			testhandler.setResult(testid, result);
		} catch (UserNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		} catch (DBException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} catch (TestNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		} catch (TestNotCompletedException e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		return Response.ok().build();
	}
	

	/**
	 * @param publicKeyMod
	 * @param publicKeyExp
	 * @return
	 */
	@POST
	@Path("start")
	public Response startTest(@FormParam("keyMod") String publicKeyMod,@FormParam("keyExp") String publicKeyExp) {
		Test createdTest = null;
		
		try {
			createdTest = testhandler.createTest(SecurityContextHolder.getContext().getAuthentication().getName(), publicKeyMod, publicKeyExp);
		} catch (UserNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		} catch (MultipleTestException e) {
			return Response.status(Status.CONFLICT).build();
		} catch (DBException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.ok(createdTest.getId()).build();
	}
	
	
	/**
	 * @param testid
	 * @return
	 */
	@GET
	@Path("{testid}/samples")
	public Response getSamples(@PathParam("testid") int testid) {
		List<String> list = Collections.emptyList();
		try {
			if(testhandler.getTest(testid).getStatus()!=TestStatus.Uncompleted) return Response.status(Status.NOT_ACCEPTABLE).build();
			list = testhandler.getTest(testid).getSampleList();
		} catch (TestNotFoundException e) {
			Response.status(Status.NOT_FOUND).build();
		} catch(DBException e) {
			Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.ok(list).build();
	}
	
	
	/**
	 * @param testid
	 * @return
	 */
	@POST
	@Path("report")
	public Response getResult(@FormParam("modulus") String modulus, @FormParam("exponent") String exponent) {
		//Test test;
		User us;
		String res;
		try {
			us = userhandler.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
			res = reporthandler.getResult(modulus, exponent, us.getCf());
		} catch(UserNotFoundException | ReportNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		} catch(DBException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.ok(res).build();
	}
	
}
