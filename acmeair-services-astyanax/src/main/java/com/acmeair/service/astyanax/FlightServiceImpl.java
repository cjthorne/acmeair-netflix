package com.acmeair.service.astyanax;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.acmeair.entities.AirportCodeMapping;
import com.acmeair.entities.Customer;
import com.acmeair.entities.Flight;
import com.acmeair.entities.FlightPK;
import com.acmeair.entities.FlightSegment;
import com.acmeair.service.FlightService;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.serializers.StringSerializer;

@Service("flightService")
public class FlightServiceImpl implements FlightService {

	private static final ColumnFamily<String, String> CF_AIRPORT_CODE_MAPPING = new ColumnFamily<String, String>("airport_code_mapping", StringSerializer.get(), StringSerializer.get());

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
		MutationBatch m = CUtils.getKeyspace().prepareMutationBatch();
		
		m.withRow(CF_AIRPORT_CODE_MAPPING, mapping.getAirportCode())
			.putColumn("airport_code", mapping.getAirportCode(), null)
			.putColumn("airport_name", mapping.getAirportName(), null);
		
		try {
		  m.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
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

	}

}
