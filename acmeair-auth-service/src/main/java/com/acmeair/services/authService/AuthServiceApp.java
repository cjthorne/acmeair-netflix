package com.acmeair.services.authService;

import com.acmeair.service.CustomerService;
import com.acmeair.service.KeyGenerator;
import com.acmeair.service.astyanax.CustomerServiceImpl;
import com.acmeair.service.astyanax.DefaultKeyGeneratorImpl;
import com.netflix.adminresources.resources.KaryonWebAdminModule;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.governator.annotations.Modules;
import com.netflix.karyon.KaryonBootstrap;
import com.netflix.karyon.archaius.ArchaiusBootstrap;
import com.netflix.karyon.eureka.KaryonEurekaModule;
import com.netflix.karyon.jersey.blocking.KaryonJerseyModule;

@ArchaiusBootstrap
@KaryonBootstrap(name = "ACMEAIR_AUTH_SERVICE")
@Modules(include = {AuthServiceApp.KaryonJerseyModuleImpl.class, KaryonWebAdminModule.class, KaryonEurekaModule.class})
public final class AuthServiceApp {
	public static class KaryonJerseyModuleImpl extends KaryonJerseyModule {

        @Override
        protected void configure() {
            super.configure();
            bind(CustomerService.class).to(CustomerServiceImpl.class);
            bind(KeyGenerator.class).to(DefaultKeyGeneratorImpl.class);
            bind(EurekaInstanceConfig.class).to(MyDataCenterInstanceConfig.class);
        }

        @Override
        public int serverPort() {
            return 8888;
        }

        @Override
        public int shutdownPort() {
            return 8899;
        }
    }
}
