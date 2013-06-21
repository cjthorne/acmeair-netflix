/*******************************************************************************
* Copyright (c) 2013 IBM Corp.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*******************************************************************************/
package com.acmeair.services;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.karyon.server.KaryonServer;
import com.netflix.karyon.spi.PropertyNames;

public class KaryonListener implements ServletContextListener {
	private static KaryonServer server;
	private static final Logger logger = LoggerFactory.getLogger(KaryonListener.class);

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		try {
			server.close();
		}
		catch (Exception e) {
			logger.error("error stopping karyon ... ", e);
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		logger.info("starting up karyon");
		
		try {
			System.setProperty("archaius.deployment.applicationId", "acmeair-auth-service");
			System.setProperty(PropertyNames.SERVER_BOOTSTRAP_BASE_PACKAGES_OVERRIDE, "com.acmeair");

			String appId = ConfigurationManager.getDeploymentContext().getApplicationId();
			String env = ConfigurationManager.getDeploymentContext().getDeploymentEnvironment();
			
			// populate the eureka-specific properties
			System.setProperty("eureka.client.props", appId);
			if (env != null) {
				System.setProperty("eureka.environment", env);
			}
			System.setProperty(DynamicPropertyFactory.ENABLE_JMX, "true");
			server = new KaryonServer();
			server.initialize();
			server.start();
		}
		catch (Exception e) {
			logger.error("error starting karyon", e);
			throw new RuntimeException("could not start karyon", e);
		}
	}
}
