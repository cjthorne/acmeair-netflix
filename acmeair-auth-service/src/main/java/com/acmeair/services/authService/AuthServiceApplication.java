package com.acmeair.services.authService;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.karyon.spi.Application;

@Application
public class AuthServiceApplication {
	private static final Logger logger = LoggerFactory.getLogger(AuthServiceApplication.class);
	
	@PostConstruct
	public void initialize() {
		logger.debug("initializing");
	}
}
