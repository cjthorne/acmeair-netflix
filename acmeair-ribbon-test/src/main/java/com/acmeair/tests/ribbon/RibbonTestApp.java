package com.acmeair.tests.ribbon;

import com.netflix.adminresources.resources.KaryonWebAdminModule;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.governator.annotations.Modules;
import com.netflix.karyon.KaryonBootstrap;
import com.netflix.karyon.archaius.ArchaiusBootstrap;
import com.netflix.karyon.eureka.KaryonEurekaModule;
import com.netflix.karyon.jersey.blocking.KaryonJerseyModule;

@ArchaiusBootstrap
@KaryonBootstrap(name = "ACMEAIR_RIBBON_TEST")
@Modules(include = {RibbonTestApp.KaryonJerseyModuleImpl.class, KaryonWebAdminModule.class, KaryonEurekaModule.class})
public final class RibbonTestApp {
	public static class KaryonJerseyModuleImpl extends KaryonJerseyModule {

        @Override
        protected void configure() {
            super.configure();
            bind(EurekaInstanceConfig.class).to(MyDataCenterInstanceConfig.class);
        }

        @Override
        public int serverPort() {
            return 9888;
        }

        @Override
        public int shutdownPort() {
            return 9899;
        }
    }
}
