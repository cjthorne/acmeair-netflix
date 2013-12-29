package com.acmeair.service.astyanax;

import org.springframework.stereotype.Service;

import com.acmeair.entities.Customer;
import com.acmeair.entities.Customer.MemberShipStatus;
import com.acmeair.entities.Customer.PhoneType;
import com.acmeair.entities.CustomerAddress;
import com.acmeair.entities.CustomerSession;
import com.acmeair.service.CustomerService;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

@Service("customerService")
public class CustomerServiceImpl implements CustomerService {

	private static final ColumnFamily<String, String> CF_CUSTOMER = new ColumnFamily<String, String>("customer", StringSerializer.get(), StringSerializer.get());
	
	private static Keyspace ks;
	
	@Override
	public Customer createCustomer(String username, String password,
			MemberShipStatus status, int total_miles, int miles_ytd,
			String phoneNumber, PhoneType phoneNumberType,
			CustomerAddress address) {
		
		MutationBatch m = getKeyspace().prepareMutationBatch();
		
		m.withRow(CF_CUSTOMER, username)
			.putColumn("username", username, null)
			.putColumn("password", password, null)
			.putColumn("customer_status", status.toString())
			.putColumn("total_miles", total_miles)
			.putColumn("miles_ytd", miles_ytd)
			.putColumn("addr_street1", address.getStreetAddress1(), null)
			.putColumn("addr_street2", address.getStreetAddress2(), null)
			.putColumn("addr_city", address.getCity(), null)
			.putColumn("addr_state_province", address.getStateProvince(), null)
			.putColumn("addr_country", address.getCountry(), null)
			.putColumn("addr_postal_code", address.getPostalCode(), null)
			.putColumn("phone_number", phoneNumber, null)
			.putColumn("phone_number_type", phoneNumberType.toString(), null);
		
		try {
		  OperationResult<Void> result = m.execute();
		  System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("loading up customer ...");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Customer updateCustomer(Customer customer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Customer getCustomerByUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean validateCustomer(String username, String password) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Customer getCustomerByUsernameAndPassword(String username,
			String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CustomerSession validateSession(String sessionid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CustomerSession createSession(String customerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void invalidateSession(String sessionid) {
		// TODO Auto-generated method stub

	}
	
	private Keyspace getKeyspace() {
		if (ks == null) {
			AstyanaxContext<Keyspace> context = new AstyanaxContext.Builder()
		    .forKeyspace("acmeair")
		    .withAstyanaxConfiguration(new AstyanaxConfigurationImpl()      
		        .setDiscoveryType(NodeDiscoveryType.RING_DESCRIBE)
		    )
		    .withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl("MyConnectionPool")
		        .setPort(9160)
		        .setMaxConnsPerHost(1)
		        .setSeeds("cass1:9160")
		    )
		    .withAstyanaxConfiguration(new AstyanaxConfigurationImpl()      
		    	.setCqlVersion("3.0.0")
		    	.setTargetCassandraVersion("1.2"))
		    .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
		    .buildKeyspace(ThriftFamilyFactory.getInstance());

			context.start();
			ks = context.getClient();
		}
		return ks;
	}

}
