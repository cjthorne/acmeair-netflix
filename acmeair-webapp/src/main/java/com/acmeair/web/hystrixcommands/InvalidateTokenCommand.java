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
package com.acmeair.web.hystrixcommands;

import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Charsets;
import com.netflix.client.ClientFactory;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.niws.client.http.HttpClientRequest;
import com.netflix.niws.client.http.HttpClientResponse;
import com.netflix.niws.client.http.RestClient;
import com.netflix.niws.client.http.HttpClientRequest.Verb;

public class InvalidateTokenCommand extends HystrixCommand<Void> {
	private static final Log log = LogFactory.getLog(InvalidateTokenCommand.class);
	private String tokenid;
	
	public InvalidateTokenCommand(String tokenid) {
        super (HystrixCommandGroupKey.Factory.asKey("AcmeAirGroup"));
        this.tokenid = tokenid;
	}
	
	@Override
	protected Void run() throws Exception {
		RestClient client = (RestClient) ClientFactory.getNamedClient("acmeair-auth-service-client");

		HttpClientRequest request = HttpClientRequest.newBuilder().setVerb(Verb.DELETE).setUri(new URI("/acmeair-auth-service-0.1.0-SNAPSHOT/rest/api/authtoken/" + tokenid)).build();
		HttpClientResponse response = client.executeWithLoadBalancer(request);
		
		String responseString = IOUtils.toString(response.getRawEntity(), Charsets.UTF_8);
		log.debug("responseString = " + responseString);
		return null;
	}
	
	@Override
	protected Void getFallback() {
		return null;
	}
}
