package com.acmeair.tests.ribbon;

import java.nio.charset.Charset;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import rx.Observable;
import rx.functions.Func1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import com.google.inject.Singleton;
import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixExecutableInfo;
import com.netflix.hystrix.HystrixObservableCommand;
//import com.netflix.ribbon.ClientOptions;
import com.netflix.ribbon.Ribbon;
import com.netflix.ribbon.http.HttpRequestTemplate;
import com.netflix.ribbon.http.HttpResourceGroup;
import com.netflix.ribbon.hystrix.FallbackHandler;

@Singleton
@Path("/rest/api/test")
public class Test {
    private final HttpResourceGroup httpResourceGroup;
    private final HttpRequestTemplate<ByteBuf> createAuthTokenTemplate;
    private final HttpRequestTemplate<ByteBuf> validateAuthTokenTemplate;
    
	public Test() {
		httpResourceGroup = Ribbon.createHttpResourceGroup("acmeair-auth-service-client");
//				ClientOptions.create()
//					.withMaxAutoRetriesNextServer(3)
//					.withConfigurationBasedServerList("localhost:" + port));
		
		createAuthTokenTemplate = httpResourceGroup.newRequestTemplate("createAuthToken", ByteBuf.class)
                .withMethod("POST")
                .withUriTemplate("/rest/api/authtoken/byuserid/{userId}")
                //.withFallbackProvider(new CreateAuthTokenFallbackHandler())
                .withHeader("SomeHeader", "SomeHeaderValue"); // TODO: Handle headers of interest

		validateAuthTokenTemplate = httpResourceGroup.newRequestTemplate("validateAuthToken", ByteBuf.class)
                .withMethod("GET")
                .withUriTemplate("/rest/api/authtoken/{tokenId}")
                //.withFallbackProvider(new ValidateAuthTokenFallbackHandler())
                .withHystrixProperties((HystrixObservableCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("tokenservicegroup"))));
	}
	
	@GET
	@Path("makeBlockingRequests")
	public void makeBlockingRequests() {
		DynamicIntProperty timeout = DynamicPropertyFactory.getInstance().getIntProperty("tokenservicegroup.execution.isolation.thread.timeoutInMilliseconds", 0);
		System.out.println("timeout = " + timeout.get());
		
		Observable<ByteBuf> oCreateToken = createAuthTokenTemplate.requestBuilder().withRequestProperty("userId", "user1").build().toObservable();
		String result1 = oCreateToken.map(new Func1<ByteBuf, String>() {
			@Override
			public String call(ByteBuf buf) {
				String string = buf.toString(Charset.defaultCharset());
				return string;
			}
		}).toBlocking().first();
		System.out.println("result1 = " + result1);
		
		Observable<ByteBuf> oValidateToken = validateAuthTokenTemplate.requestBuilder().withRequestProperty("tokenId", "40a00e1d-b138-4404-a91f-09a965246414").build().toObservable();
		String result2 = oValidateToken.map(new Func1<ByteBuf, String>() {
			@Override
			public String call(ByteBuf buf) {
				String string = buf.toString(Charset.defaultCharset());
				return string;
			}
		}).toBlocking().first();
		System.out.println("result2 = " + result2);
		
		Hystrix.reset();
	}
    
//    public static void main(String args[]) {
//    	Test t = new Test(8888);
//    	t.makeBlockingRequests();
//    }
    
//    private class ValidateAuthTokenFallbackHandler implements FallbackHandler<ByteBuf> {
//		@Override
//		public Observable<ByteBuf> getFallback(HystrixExecutableInfo<?> hystrixInfo, Map<String, Object> requestProperties) {
//			// TODO: Do something more useful
//	        byte[] bytes = "{}".getBytes(Charset.defaultCharset());
//	        ByteBuf byteBuf = UnpooledByteBufAllocator.DEFAULT.buffer(bytes.length);
//	        byteBuf.writeBytes(bytes);
//	        return Observable.just(byteBuf);
//        }
//    }
//    
//    private class CreateAuthTokenFallbackHandler implements FallbackHandler<ByteBuf> {
//		@Override
//		public Observable<ByteBuf> getFallback(HystrixExecutableInfo<?> hystrixInfo, Map<String, Object> requestProperties) {
//			// TODO: Do something more useful
//	        byte[] bytes = "{}".getBytes(Charset.defaultCharset());
//	        ByteBuf byteBuf = UnpooledByteBufAllocator.DEFAULT.buffer(bytes.length);
//	        byteBuf.writeBytes(bytes);
//	        return Observable.just(byteBuf);
//        }
//    }
}
