package com.acmeair.web;

import com.acmeair.service.BookingService;
import com.acmeair.service.CustomerService;
import com.acmeair.service.FlightService;
import com.acmeair.service.KeyGenerator;
import com.acmeair.service.cassandra.BookingServiceImpl;
import com.acmeair.service.cassandra.CustomerServiceImpl;
import com.acmeair.service.cassandra.DefaultKeyGeneratorImpl;
import com.acmeair.service.cassandra.FlightServiceImpl;
import com.google.inject.Module;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class WebAppGuiceContextListener extends GuiceServletContextListener {
	public static Injector webAppInjector;
	
	public WebAppGuiceContextListener() {
	}
	
	@Override
	protected Injector getInjector() {
		Injector i = Guice.createInjector(new WebAppGuiceModule());
		webAppInjector = i;
		return i;
	}
	
	public static Injector getWebAppInjector() {
		return webAppInjector;
	}
	
	class WebAppGuiceModule implements Module {
		public void configure(Binder binder) {
			binder.bind(CustomerService.class).to(CustomerServiceImpl.class);
			binder.bind(BookingService.class).to(BookingServiceImpl.class);
			binder.bind(FlightService.class).to(FlightServiceImpl.class);
			binder.bind(KeyGenerator.class).to(DefaultKeyGeneratorImpl.class);
		}
	}
}
