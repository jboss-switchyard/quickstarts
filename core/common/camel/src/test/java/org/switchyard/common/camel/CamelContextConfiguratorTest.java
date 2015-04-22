/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.common.camel;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.ManagementStatisticsLevel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.MockDomain;
import org.switchyard.ServiceDomain;

/**
 * Test configuration of CamelContext through properties and a callback class.
 */
public class CamelContextConfiguratorTest {

    private SwitchYardCamelContextImpl context;
    private ServiceDomain domain;

    @Before
    public void setUp() {
        context = new SwitchYardCamelContextImpl();
        domain = new MockDomain();
        context.setServiceDomain(domain);
    }

	@Test
    public void configureShutdownTimeout() throws Exception {
	    final int timeout = 12345;
        domain.setProperty(CamelContextConfigurator.SHUTDOWN_TIMEOUT, timeout);
        CamelContextConfigurator.configure(context, domain);
        Assert.assertEquals(timeout, context.getShutdownStrategy().getTimeout());
    }
	
	@Test
    public void configurePerformanceStatistics() throws Exception {
	    domain.setProperty(CamelContextConfigurator.PERFORMANCE_STATISTICS, "RoutesOnly");
        CamelContextConfigurator.configure(context, domain);
        Assert.assertEquals(ManagementStatisticsLevel.RoutesOnly, 
                context.getManagementStrategy().getStatisticsLevel());
        
        domain.setProperty(CamelContextConfigurator.PERFORMANCE_STATISTICS, "Off");
        CamelContextConfigurator.configure(context, domain);
        Assert.assertEquals(ManagementStatisticsLevel.Off, 
                context.getManagementStrategy().getStatisticsLevel());
        
        domain.setProperty(CamelContextConfigurator.PERFORMANCE_STATISTICS, "All");
        CamelContextConfigurator.configure(context, domain);
        Assert.assertEquals(ManagementStatisticsLevel.All, 
                context.getManagementStrategy().getStatisticsLevel());
    }
	
	@Test
	public void configureCamelContextAware() throws Exception {
	    // specify the CamelContextAware class
	    domain.setProperty(CamelContextConfigurator.CAMEL_CONTEXT_CONFIG, 
	            MyCustomContextAware.class.getName());
	    
	    // configure the context and check results
	    CamelContextConfigurator.configure(context, domain);
	    Assert.assertNotNull(context.getProperty("abc"));
        Assert.assertEquals("xyz", context.getProperty("abc"));
	}
}

class MyCustomContextAware implements CamelContextAware {
    
    private CamelContext context;
    private Map<String, String> customProps;
    
    public MyCustomContextAware() {
        customProps = new HashMap<String, String>();
        customProps.put("abc", "xyz");
    }
    
    MyCustomContextAware(Map<String, String> props) {
        customProps = props;
    }

    @Override
    public void setCamelContext(CamelContext camelContext) {
        context = camelContext;
        camelContext.setProperties(customProps);
    }

    @Override
    public CamelContext getCamelContext() {
        return context;
    }
    
}
