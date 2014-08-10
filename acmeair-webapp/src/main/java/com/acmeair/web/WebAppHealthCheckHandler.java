package com.acmeair.web;

import com.netflix.karyon.spi.HealthCheckHandler;

public class WebAppHealthCheckHandler implements HealthCheckHandler {
	@Override
	public int getStatus() {
		return 200;
	}
}
