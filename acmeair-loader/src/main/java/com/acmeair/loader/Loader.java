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
package com.acmeair.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.netflix.config.DynamicPropertyFactory;

public class Loader {
	private static Logger logger = LoggerFactory.getLogger(Loader.class);

	public static void main(String args[]) throws Exception {
		Loader loader = new Loader();
		loader.load();
	}
	
	private void load() {
		
		Injector injector = Guice.createInjector(new LoaderGuiceModule());
	    
		try {
			FlightLoader flightLoader = injector.getInstance(FlightLoader.class);
			CustomerLoader customerLoader = injector.getInstance(CustomerLoader.class);
			long start = System.currentTimeMillis();
			logger.info("Start loading flights");
			flightLoader.loadFlights();
			logger.info("Ended loading flights");
			logger.info("Start loading customers");
			customerLoader.loadCustomers();
			logger.info("Ended loading customers");
			long stop = System.currentTimeMillis();

			// TODO: Needed in datastax based loader, if removed, remove from here too
			flightLoader.closeDatasource();
			
			logger.info("Finished loading in " + (stop - start)/1000.0 + " seconds");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}	
}