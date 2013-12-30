package com.acmeair.service.astyanax;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.acmeair.entities.Customer;
import com.acmeair.entities.Customer.MemberShipStatus;
import com.acmeair.entities.Customer.PhoneType;
import com.acmeair.entities.CustomerAddress;
import com.acmeair.entities.CustomerSession;
import com.acmeair.service.CustomerService;
import com.acmeair.service.KeyGenerator;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.serializers.StringSerializer;

@Service("customerService")
public class CustomerServiceImpl implements CustomerService {

	private static final int DAYS_TO_ALLOW_SESSION = 1;

	private static final ColumnFamily<String, String> CF_CUSTOMER = new ColumnFamily<String, String>("customer", StringSerializer.get(), StringSerializer.get());
	private static final ColumnFamily<String, String> CF_CUSTOMER_SESSION = new ColumnFamily<String, String>("customer_session", StringSerializer.get(), StringSerializer.get());
	
	@Resource
	KeyGenerator keyGenerator;

	@Override
	public Customer createCustomer(String username, String password,
			MemberShipStatus status, int total_miles, int miles_ytd,
			String phoneNumber, PhoneType phoneNumberType,
			CustomerAddress address) {
		
		Customer customer = new Customer(username, password, status, total_miles, miles_ytd, address, phoneNumber, phoneNumberType);

		MutationBatch m = CUtils.getKeyspace().prepareMutationBatch();
		
		m.withRow(CF_CUSTOMER, username)
			.putColumn("username", username, null)
			.putColumn("password", password, null)
			.putColumn("customer_status", status.toString(), null)
			.putColumn("total_miles", total_miles, null)
			.putColumn("miles_ytd", miles_ytd, null)
			.putColumn("addr_street1", address.getStreetAddress1(), null)
			.putColumn("addr_street2", address.getStreetAddress2(), null)
			.putColumn("addr_city", address.getCity(), null)
			.putColumn("addr_state_province", address.getStateProvince(), null)
			.putColumn("addr_country", address.getCountry(), null)
			.putColumn("addr_postal_code", address.getPostalCode(), null)
			.putColumn("phone_number", phoneNumber, null)
			.putColumn("phone_number_type", phoneNumberType.toString(), null);
		
		try {
		  m.execute();
		  return customer;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
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
		try {
			ColumnList<String> result = CUtils.getKeyspace().prepareQuery(CF_CUSTOMER_SESSION)
				.getKey(sessionid).execute().getResult();
				if (!result.isEmpty()) {
					Date now = new Date();
					Date timeoutTime = result.getDateValue("timeout_time", null);
					if (timeoutTime.before(now)) {
						return null;
					}
					Date lastAccessedTime = result.getDateValue("last_accessed_time", null);
					String customerid = result.getStringValue("customer_id", null);
					String id = result.getStringValue("session_id", null);
					CustomerSession cs = new CustomerSession(id, customerid, lastAccessedTime, timeoutTime);
					return cs;
				}
				else {
					return null;
				}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public CustomerSession createSession(String customerId) {
		String sessionId = keyGenerator.generate().toString();
		Date now = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		c.add(Calendar.DAY_OF_YEAR, DAYS_TO_ALLOW_SESSION);
		Date expiration = c.getTime();
		CustomerSession cSession = new CustomerSession(sessionId, customerId, now, expiration);
		
		MutationBatch m = CUtils.getKeyspace().prepareMutationBatch();

		m.withRow(CF_CUSTOMER_SESSION, cSession.getId())
			.putColumn("session_id", cSession.getId(), null)
			.putColumn("customer_id", cSession.getCustomerid(), null)
			.putColumn("last_accessed_time", cSession.getLastAccessedTime(), null)
			.putColumn("timeout_time", cSession.getTimeoutTime(), null);
		
		try {
		  m.execute();
		  return cSession;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void invalidateSession(String sessionid) {
		MutationBatch m = CUtils.getKeyspace().prepareMutationBatch();

		m.withRow(CF_CUSTOMER_SESSION, sessionid).delete();
		
		try {
		  m.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
