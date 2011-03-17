/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard.bus.hornetq;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.Exchange;
import org.switchyard.ServiceReference;
import org.switchyard.internal.DefaultHandlerChain;
import org.switchyard.internal.ExchangeImpl;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.InOutService;
import org.switchyard.spi.Dispatcher;

public class HornetQBusTest {

    private HornetQBus _provider;

    @Before
    public void setUp() throws Exception {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put(HornetQBus.WORK_DIR, "target/hornetQ");
        _provider = new HornetQBus(config);
        _provider.start();
    }
    
    @After
    public void tearDown() throws Exception {
        _provider.stop();
    }
    
    @Test
    public void testCreateDispatcher() throws Exception {
        // verify that dispatchers can be created for an InOnly service
        _provider.createDispatcher(
                new MockServiceReference(new QName("inOnly"), new InOnlyService()), 
                new DefaultHandlerChain());

        // verify that dispatchers can be created for an InOut service
        _provider.createDispatcher(
                new MockServiceReference(new QName("inOut"), new InOutService()), 
                new DefaultHandlerChain());
    }
    
    @Test
    public void testGetDispatcher() throws Exception {
        ServiceReference service = new MockServiceReference(new QName("testGetDispatcher"));
        Dispatcher dispatch = _provider.createDispatcher(service, new DefaultHandlerChain());
        
        Assert.assertEquals(dispatch, _provider.getDispatcher(service));
    }
    
    @Test
    public void testUseExistingHornetQConfig() throws Exception {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put(HornetQBus.WORK_DIR, "target/configTest");
        config.put(HornetQBus.CONFIG_PATH, "configTest/hornetq-configuration.xml");
        config.put(HornetQBus.SERVER_ID, "1");
        HornetQBus bus = new HornetQBus(config);
        bus.start();
        bus.stop();
    }
}
