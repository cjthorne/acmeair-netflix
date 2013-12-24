package com.acmeair.service.astyanax;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.acmeair.entities.AirportCodeMapping;
import com.acmeair.entities.Flight;
import com.acmeair.entities.FlightPK;
import com.acmeair.entities.FlightSegment;
import com.acmeair.service.FlightService;

@Service("flightService")
public class FlightServiceImpl implements FlightService {

	@Override
	public Flight getFlightByFlightKey(FlightPK key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Flight> getFlightByAirportsAndDepartureDate(String fromAirport,
			String toAirport, Date deptDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Flight> getFlightByAirports(String fromAirport, String toAirport) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void storeAirportMapping(AirportCodeMapping mapping) {
		// TODO Auto-generated method stub

	}

	@Override
	public Flight createNewFlight(String flightSegmentId,
			Date scheduledDepartureTime, Date scheduledArrivalTime,
			BigDecimal firstClassBaseCost, BigDecimal economyClassBaseCost,
			int numFirstClassSeats, int numEconomyClassSeats,
			String airplaneTypeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void storeFlightSegment(FlightSegment flightSeg) {
		// TODO Auto-generated method stub

	}

}
