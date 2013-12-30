package com.acmeair.service.astyanax;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.acmeair.entities.AirportCodeMapping;
import com.acmeair.entities.Flight;
import com.acmeair.entities.FlightPK;
import com.acmeair.entities.FlightSegment;
import com.acmeair.service.FlightService;
import com.acmeair.service.KeyGenerator;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.serializers.StringSerializer;

@Service("flightService")
public class FlightServiceImpl implements FlightService {

	private static final ColumnFamily<String, String> CF_AIRPORT_CODE_MAPPING = new ColumnFamily<String, String>("airport_code_mapping", StringSerializer.get(), StringSerializer.get());
	private static final ColumnFamily<String, String> CF_FLIGHT_SEGMENT = new ColumnFamily<String, String>("flight_segment", StringSerializer.get(), StringSerializer.get());
	private static final ColumnFamily<String, String> CF_FLIGHT = new ColumnFamily<String, String>("flight", StringSerializer.get(), StringSerializer.get());

	@Resource
	KeyGenerator keyGenerator;

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
		String id = keyGenerator.generate().toString();
		Flight flight = new Flight(id, flightSegmentId,
			scheduledDepartureTime, scheduledArrivalTime,
			firstClassBaseCost, economyClassBaseCost,
			numFirstClassSeats, numEconomyClassSeats,
			airplaneTypeId);
		
		MutationBatch m = CUtils.getKeyspace().prepareMutationBatch();
		
		m.withRow(CF_FLIGHT, flight.getPkey().getId())
			.putColumn("flight_id", flight.getPkey().getId(), null)
			.putColumn("flight_segment_id", flight.getPkey().getFlightSegmentId(), null)
			.putColumn("scheduled_departure_time", flight.getScheduledDepartureTime(), null)
			.putColumn("scheduled_arrival_time", flight.getScheduledArrivalTime(), null)
			// TODO:  why doesn't putColumn support BigDecimal
			.putColumn("first_class_base_cost", flight.getFirstClassBaseCost().floatValue(), null)
			// TODO:  why doesn't putColumn support BigDecimal
			.putColumn("economy_class_base_cost", flight.getEconomyClassBaseCost().floatValue(), null)
			.putColumn("num_first_class_seats", flight.getNumFirstClassSeats(), null)
			.putColumn("num_economy_class_seats", flight.getNumEconomyClassSeats(), null)
			.putColumn("airplane_type_id", flight.getAirplaneTypeId(), null);
		
		try {
		  m.execute();
		  return flight;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	@Override
	public void storeFlightSegment(FlightSegment flightSeg) {
		MutationBatch m = CUtils.getKeyspace().prepareMutationBatch();

		m.withRow(CF_FLIGHT_SEGMENT, flightSeg.getFlightName())
			.putColumn("flight_segment_id", flightSeg.getFlightName(), null)
			.putColumn("origin_port", flightSeg.getOriginPort(), null)
			.putColumn("dest_port", flightSeg.getDestPort(), null)
			.putColumn("miles", flightSeg.getMiles(), null);

		try {
		  m.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
