package com.acmeair.service.astyanax;

import javax.inject.Singleton;

import com.acmeair.service.KeyGenerator;

@Singleton
public class DefaultKeyGeneratorImpl implements KeyGenerator {

	@Override
	public Object generate() {
		return java.util.UUID.randomUUID().toString();
	}
}