package com.acmeair.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acmeair.service.CustomerService;
import com.acmeair.service.FlightService;
import com.acmeair.service.KeyGenerator;
import com.acmeair.service.cassandra.CustomerServiceImpl;
import com.acmeair.service.cassandra.DefaultKeyGeneratorImpl;
import com.acmeair.service.cassandra.FlightServiceImpl;
import com.google.inject.Binder;
import com.google.inject.Module;

public class LoaderGuiceModule implements Module {
	private static Logger logger = LoggerFactory.getLogger(LoaderGuiceModule.class);
	
	@Override
	public void configure(Binder binder) {
		logger.debug("doing binding");
		binder.bind(CustomerService.class).to(CustomerServiceImpl.class);
		binder.bind(FlightService.class).to(FlightServiceImpl.class);
		binder.bind(KeyGenerator.class).to(DefaultKeyGeneratorImpl.class);
	}
}
