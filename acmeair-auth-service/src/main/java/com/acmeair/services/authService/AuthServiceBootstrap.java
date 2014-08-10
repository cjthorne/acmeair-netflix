package com.acmeair.services.authService;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acmeair.service.CustomerService;
import com.acmeair.service.KeyGenerator;
import com.acmeair.service.cassandra.CustomerServiceImpl;
import com.acmeair.service.cassandra.DefaultKeyGeneratorImpl;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.netflix.governator.guice.LifecycleInjectorBuilder;
import com.netflix.karyon.server.ServerBootstrap;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class AuthServiceBootstrap extends ServerBootstrap {
	private static final Logger logger = LoggerFactory.getLogger(AuthServiceBootstrap.class);

	@Override
	protected void beforeInjectorCreation(LifecycleInjectorBuilder builderToBeUsed) {
		builderToBeUsed.withAdditionalModules(
			new JerseyServletModule() {
				@Override
				protected void configureServlets() {
					Map<String, String> params = new HashMap<String, String>();
					params.put(PackagesResourceConfig.PROPERTY_PACKAGES, "com.acmeair.services.authService");
					params.put(JSONConfiguration.FEATURE_POJO_MAPPING, "true");
					serve("/rest/api/*").with(GuiceContainer.class, params);
					binder().bind(GuiceContainer.class).asEagerSingleton();
				}
			},
			new AuthServiceGuiceModule()
		);
	}
	
	class AuthServiceGuiceModule implements Module {
		public void configure(Binder binder) {
			binder.bind(CustomerService.class).to(CustomerServiceImpl.class);
			binder.bind(KeyGenerator.class).to(DefaultKeyGeneratorImpl.class);
		}
	}
}
