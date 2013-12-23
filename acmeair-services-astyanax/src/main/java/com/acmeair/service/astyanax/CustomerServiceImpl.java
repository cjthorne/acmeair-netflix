package com.acmeair.service.astyanax;

import com.acmeair.entities.Customer;
import com.acmeair.entities.Customer.MemberShipStatus;
import com.acmeair.entities.Customer.PhoneType;
import com.acmeair.entities.CustomerAddress;
import com.acmeair.entities.CustomerSession;
import com.acmeair.service.CustomerService;

public class CustomerServiceImpl implements CustomerService {

	@Override
	public Customer createCustomer(String username, String password,
			MemberShipStatus status, int total_miles, int miles_ytd,
			String phoneNumber, PhoneType phoneNumberType,
			CustomerAddress address) {
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

}
