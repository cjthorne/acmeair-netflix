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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.acmeair.entities.AirportCodeMapping;
import com.acmeair.entities.Flight;
import com.acmeair.entities.FlightPK;
import com.acmeair.entities.FlightSegment;
import com.acmeair.service.FlightService;
import com.acmeair.service.KeyGenerator;

import org.springframework.stereotype.Service;

@Service("flightService")
public class FlightServiceImpl implements FlightService {

	@Resource
	KeyGenerator keyGenerator;

	@Override
	public Flight getFlightByFlightKey(FlightPK key) {
		Date now = new Date();
		Flight flight = new Flight(key.getId(), key.getFlightSegmentId(), now, now,
				new BigDecimal(500), new BigDecimal(200), 10, 200, "B747");
		return flight;
	}

	@Override
	public List<Flight> getFlightByAirportsAndDepartureDate(String fromAirport, String toAirport, Date deptDate) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public List<Flight> getFlightByAirports(String fromAirport, String toAirport) {
		Flight flight = null;
		if (fromAirport.equals("CDG")) {
			FlightSegment flightSeg = new FlightSegment("AA1", "CDG", "JFK", 1);
			Date now = new Date();
			String id = keyGenerator.generate().toString();
			flight = new Flight(id, "AA1", now, now,
				new BigDecimal(500), new BigDecimal(200), 10, 200, "B747");		
			flight.setFlightSegment(flightSeg);
		}
		else if (fromAirport.equals("JFK")) {
			FlightSegment flightSeg = new FlightSegment("AA2", "JFK", "CDG", 1);
			Date now = new Date();
			String id = keyGenerator.generate().toString();
			flight = new Flight(id, "AA2", now, now,
				new BigDecimal(500), new BigDecimal(200), 10, 200, "B747");		
			flight.setFlightSegment(flightSeg);
		}
		List<Flight> flights = new ArrayList<Flight>();
		flights.add(flight);
		return flights;
	}
	
	
	
	@Override
	public void storeAirportMapping(AirportCodeMapping mapping) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Flight createNewFlight(String flightSegmentId,
			Date scheduledDepartureTime, Date scheduledArrivalTime,
			BigDecimal firstClassBaseCost, BigDecimal economyClassBaseCost,
			int numFirstClassSeats, int numEconomyClassSeats,
			String airplaneTypeId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void storeFlightSegment(FlightSegment flightSeg) {
		throw new UnsupportedOperationException();
	}

}
