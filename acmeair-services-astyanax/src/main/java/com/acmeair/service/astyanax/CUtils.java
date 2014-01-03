package com.acmeair.service.astyanax;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
//import com.netflix.astyanax.AstyanaxContext;
//import com.netflix.astyanax.Keyspace;
//import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
//import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
//import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
//import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
//import com.netflix.astyanax.thrift.ThriftFamilyFactory;

public class CUtils {

//	private static Keyspace ks;
	private static Session aaSess;

//	public static Keyspace getKeyspace() {
//		if (ks == null) {
//			AstyanaxContext<Keyspace> context = new AstyanaxContext.Builder()
//		    .forKeyspace("acmeair")
//		    .withAstyanaxConfiguration(new AstyanaxConfigurationImpl()      
//		        .setDiscoveryType(NodeDiscoveryType.RING_DESCRIBE)
//		    )
//		    .withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl("MyConnectionPool")
//		        .setPort(9160)
//		        .setMaxConnsPerHost(1)
//		        .setSeeds("cass1:9160")
//		    )
//		    .withAstyanaxConfiguration(new AstyanaxConfigurationImpl()      
//		    	.setCqlVersion("3.0.0")
//		    	.setTargetCassandraVersion("1.2"))
//		    .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
//		    .buildKeyspace(ThriftFamilyFactory.getInstance());
//	
//			context.start();
//			ks = context.getClient();
//		}
//		return ks;
//	}

	public static Session getAcmeAirSession() {
		if (aaSess == null) {
			Cluster cluster = Cluster.builder()
			         .addContactPoint("cass1")
			         .build();
			aaSess = cluster.connect("acmeair");
		}
		return aaSess;
	}
}
