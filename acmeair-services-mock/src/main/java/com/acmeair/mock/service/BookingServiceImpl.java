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
package com.acmeair.mock.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.acmeair.entities.Booking;
import com.acmeair.entities.BookingPK;
import com.acmeair.entities.Customer;
import com.acmeair.entities.Flight;
import com.acmeair.entities.FlightPK;
import com.acmeair.service.BookingService;
import com.acmeair.service.CustomerService;
import com.acmeair.service.FlightService;
import com.acmeair.service.KeyGenerator;

@Service("bookingService")
public class BookingServiceImpl implements BookingService  {
	
	//private static final Log log = LogFactory.getLog(BookingServiceImpl.class);
	
	@Resource
	FlightService flightService;

	@Resource
	CustomerService customerService;
	
	@Resource
	KeyGenerator keyGenerator;
	
	@Override
	public BookingPK bookFlight(String customerId, FlightPK flightId) {
		try{
			// We still delegate to the flight and customer service for the map access than getting the map instance directly
			Flight f = flightService.getFlightByFlightKey(flightId);
			Customer c = customerService.getCustomerByUsername(customerId);
			
			Booking newBooking = new Booking(keyGenerator.generate().toString(), new Date(), c, f);
			BookingPK key = newBooking.getPkey();
			
			return key;
		}catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public Booking getBooking(String user, String id) {
		
		try{
			Booking booking = new Booking(keyGenerator.generate().toString(), new Date(), null, null);
			
			return booking;
		}catch (Exception e)
		{
			throw new RuntimeException(e);
		}
			
	}

	@Override
	public void cancelBooking(String user, String id) {
	}	
	
	@Override
	public List<Booking> getBookingsByUser(String user) {
		try{
			Booking booking  = getBooking(user, null);
			List<Booking> bookings = new ArrayList<Booking>();
			bookings.add(booking);
			return bookings;
		}catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		
	}
}
