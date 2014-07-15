package com.acmeair.services.authService;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.config.DynamicBooleanProperty;
import com.netflix.config.DynamicPropertyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acmeair.entities.CustomerSession;
import com.acmeair.service.CustomerService;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Singleton
@Path("/rest/api/authtoken")
public class AuthServiceResource {
	private static final Logger logger = LoggerFactory.getLogger(AuthServiceResource.class);
	// TODO:  understand how I can separate healthcheck onto another service, so it's not on the same port
	//private final AuthServiceHealthCheckHandler healthCheck;
	private static DynamicBooleanProperty slowRequests = DynamicPropertyFactory.getInstance().getBooleanProperty("com.acmeair.authservice.slow", false);
	
	@Inject
	private CustomerService customerService;


	public AuthServiceResource() {
		//healthCheck = new AuthServiceHealthCheckHandler();
	}
	
	@POST
	@Path("/byuserid/{userid}")
	@Produces("application/json")
	public /* CustomerSession */ Response createToken(@PathParam("userid") String userid) {
		logger.warn("request to create token from user " + userid);
		CustomerSession cs = customerService.createSession(userid);
		return Response.ok(cs).build();
	}
	
	@GET
	@Path("{tokenid}")
	@Produces("application/json")
	public Response validateToken(@PathParam("tokenid") String tokenid) {
		System.out.println("slowreqests = " + slowRequests.get());
		if (slowRequests.get()) {
			logger.info("*** being slow");
			try { Thread.sleep(2000); } catch (Exception e) { e.printStackTrace(); }
		}
		logger.warn("request to validate token " + tokenid);
		CustomerSession cs = customerService.validateSession(tokenid);
		if (cs == null) {
			throw new WebApplicationException(404);
		}
		else {
			return Response.ok(cs).build();
		}
	}
	
	@DELETE
	@Path("{tokenid}")
	@Produces("application/json")
	public Response invalidateToken(@PathParam("tokenid") String tokenid) {
		customerService.invalidateSession(tokenid);
		return Response.ok().build();
	}	
}