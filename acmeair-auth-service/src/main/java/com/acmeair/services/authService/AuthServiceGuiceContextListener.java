package com.acmeair.services.authService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acmeair.service.CustomerService;
import com.acmeair.service.KeyGenerator;
import com.acmeair.service.cassandra.CustomerServiceImpl;
import com.acmeair.service.cassandra.DefaultKeyGeneratorImpl;
import com.google.inject.Module;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class AuthServiceGuiceContextListener extends GuiceServletContextListener {
	private static final Logger logger = LoggerFactory.getLogger(AuthServiceGuiceContextListener.class);
	
	public static Injector authInjector;
	
	public AuthServiceGuiceContextListener() {
	}
	
	@Override
	protected Injector getInjector() {
		Injector i = Guice.createInjector(new AuthServiceGuiceModule());
		authInjector = i;
		return i;
	}
	
	public static Injector getAuthInjector() {
		return authInjector;
	}
	
	class AuthServiceGuiceModule implements Module {
		public void configure(Binder binder) {
			binder.bind(CustomerService.class).to(CustomerServiceImpl.class);
			binder.bind(KeyGenerator.class).to(DefaultKeyGeneratorImpl.class);
		}
	}
}
