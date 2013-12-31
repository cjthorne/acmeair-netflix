package com.acmeair.service.astyanax;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private static Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
	
	@Resource
	KeyGenerator keyGenerator;

	public Customer createCustomer(String username, String password,
			MemberShipStatus status, int total_miles, int miles_ytd,
			String phoneNumber, PhoneType phoneNumberType,
			CustomerAddress address) {
		Customer customer = new Customer(username, password, status, total_miles, miles_ytd, address, phoneNumber, phoneNumberType);
		return upsertCustomer(customer);
	}
	
	private Customer upsertCustomer(Customer customer) {
		MutationBatch m = CUtils.getKeyspace().prepareMutationBatch();
		
		m.withRow(CF_CUSTOMER, customer.getUsername())
			.putColumn("password", customer.getPassword(), null)
			.putColumn("customer_status", customer.getStatus().toString(), null)
			.putColumn("total_miles", customer.getTotal_miles(), null)
			.putColumn("miles_ytd", customer.getMiles_ytd(), null)
			.putColumn("addr_street1", customer.getAddress().getStreetAddress1(), null)
			.putColumn("addr_street2", customer.getAddress().getStreetAddress2(), null)
			.putColumn("addr_city", customer.getAddress().getCity(), null)
			.putColumn("addr_state_province", customer.getAddress().getStateProvince(), null)
			.putColumn("addr_country", customer.getAddress().getCountry(), null)
			.putColumn("addr_postal_code", customer.getAddress().getPostalCode(), null)
			.putColumn("phone_number", customer.getPhoneNumber(), null)
			.putColumn("phone_number_type", customer.getPhoneNumberType().toString(), null);
		
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
		return upsertCustomer(customer);
	}

	private Customer getCustomer(String username) {
		try {
			ColumnList<String> result = CUtils.getKeyspace().prepareQuery(CF_CUSTOMER)
				.getKey(username).execute().getResult();
				if (!result.isEmpty()) {
					CustomerAddress address = new CustomerAddress(
						result.getStringValue("addr_street1", null),
						result.getStringValue("addr_street2", null),
						result.getStringValue("addr_city", null),
						result.getStringValue("addr_state_province", null),
						result.getStringValue("addr_country", null),
						result.getStringValue("addr_postal_code", null)
					);
					PhoneType phoneType = PhoneType.valueOf(result.getStringValue("phone_number_type", PhoneType.HOME.toString()));
					MemberShipStatus memStatus = MemberShipStatus.valueOf(result.getStringValue("customer_status", MemberShipStatus.NONE.toString()));
					Customer customer = new Customer(
						username,
						result.getStringValue("password", null),
						memStatus,
						result.getIntegerValue("total_miles", 0),
						result.getIntegerValue("miles_ytd", 0),
						address,
						result.getStringValue("phone_number", null),
						phoneType
					);
					return customer;
				}
				else {
					return null;
				}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Customer getCustomerByUsername(String username) {
		Customer c = getCustomer(username);
		if (c != null) {
			c.setPassword(null);
		}
		return c;
	}

	@Override
	public boolean validateCustomer(String username, String password) {
		boolean validatedCustomer = false;
		Customer customerToValidate = getCustomer(username);
		if (customerToValidate != null) {
			validatedCustomer = password.equals(customerToValidate.getPassword());
		}
		return validatedCustomer;
	}

	@Override
	public Customer getCustomerByUsernameAndPassword(String username,
			String password) {
		Customer c = getCustomer(username);
		if (!c.getPassword().equals(password)) {
			return null;
		}
		// Should we also set the password to null?
		return c;
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
