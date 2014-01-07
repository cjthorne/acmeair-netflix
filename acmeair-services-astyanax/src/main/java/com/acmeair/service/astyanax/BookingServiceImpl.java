package com.acmeair.service.astyanax;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.acmeair.entities.Booking;
import com.acmeair.entities.BookingPK;
import com.acmeair.entities.Customer;
import com.acmeair.entities.Flight;
import com.acmeair.entities.FlightPK;
import com.acmeair.service.BookingService;
import com.acmeair.service.CustomerService;
import com.acmeair.service.FlightService;
import com.acmeair.service.KeyGenerator;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import org.springframework.stereotype.Service;


@Service("bookingService")
public class BookingServiceImpl implements BookingService {

	@Resource
	FlightService flightService;

	@Resource
	CustomerService customerService;

	private static PreparedStatement INSERT_INTO_BOOKING_PS;

	@Resource
	KeyGenerator keyGenerator;

	static {
		prepareStatements();
	}
	
	private static void prepareStatements() {
		INSERT_INTO_BOOKING_PS = CUtils.getAcmeAirSession().prepare(
			"INSERT INTO booking (customer_id, booking_id, flight_id, booking_date) VALUES (?, ?, ?, ?);"
		);
	}

	@Override
	public BookingPK bookFlight(String customerId, FlightPK flightId) {
		String bookingId = keyGenerator.generate().toString();
		Date dateOfBooking = new Date();
		BookingPK key = new BookingPK(customerId, bookingId);
			
		BoundStatement bs = new BoundStatement(INSERT_INTO_BOOKING_PS);
		bs.bind(customerId, bookingId, flightId.getId(), dateOfBooking);
		ResultSet rs = CUtils.getAcmeAirSession().execute(bs);
		return key;
	}

	@Override
	public Booking getBooking(String user, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Booking> getBookingsByUser(String user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cancelBooking(String user, String id) {
		// TODO Auto-generated method stub

	}

}
