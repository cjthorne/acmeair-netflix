package com.acmeair.service.cassandra;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.config.DynamicBooleanProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.DiscoveryManager;

public class CUtils {
	private static Session aaSess;
	
	private static final Log log = LogFactory.getLog(CUtils.class);
	static DynamicStringProperty cContactPoint = DynamicPropertyFactory.getInstance().getStringProperty("com.acmeair.cassandra.contactpoint", "cass1");
	static DynamicBooleanProperty cUseEureka = DynamicPropertyFactory.getInstance().getBooleanProperty("com.acmeair.cassandra.useEureka", false);
	static DynamicStringProperty cEurekaName = DynamicPropertyFactory.getInstance().getStringProperty("com.acmeair.cassandra.eurekaVipAddress", "ACMEAIR_CASSANDRA");
	static String curContactPoint = "unknown";

	public static Session getAcmeAirSession() {
		String contactPoint = null;
		if (cUseEureka.get()) {
			contactPoint = getFirstUpCassandraServer();
		}
		else {
			contactPoint = cContactPoint.get();
		}
		
		if (!contactPoint.equals(curContactPoint)) {
			synchronized(curContactPoint) {
				// a different thread already got through sync block since the change
				if (contactPoint.equals(curContactPoint)) {
					return aaSess;
				}
				
				// shut down a different old connection
				try {
					if  (aaSess != null) {
						aaSess.getCluster().shutdown();
					}
				}
				catch (Exception e) {
					log.error("could not close existing cluster", e);
				}
				
				Cluster cluster = Cluster.builder().addContactPoint(contactPoint).build();
				aaSess = cluster.connect("acmeair");
				curContactPoint = contactPoint;
			}
		}
			
		return aaSess;
	}
	
	private static String getFirstUpCassandraServer() {
		DiscoveryClient discoveryClient = DiscoveryManager.getInstance().getDiscoveryClient();
		List<InstanceInfo> listOfinstanceInfo = discoveryClient.getInstancesByVipAddress(cEurekaName.get(), false);
		for (InstanceInfo info : listOfinstanceInfo) {
			if (info.getStatus() == InstanceInfo.InstanceStatus.UP) {
				return info.getIPAddr();
			}
		}
		return null;
	}
}
