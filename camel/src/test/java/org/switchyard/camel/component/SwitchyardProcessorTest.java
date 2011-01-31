/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.camel.component;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.xml.namespace.QName;

import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardTestCase;

/**
 * Test for {@link SwitchyardProcessor}.
 * 
 * @author Daniel Bevenius
 *
 */
public class SwitchyardProcessorTest extends SwitchYardTestCase {
    private QName _serviceName = new QName("testService");
    private MockHandler _mockService;
    private ServiceDomain _domain;
    private ServiceReference _registerService;
    
    @Before
    public void setup() {
        _mockService = new MockHandler();
        _domain = getServiceDomain();
        _registerService = _domain.registerService(_serviceName, _mockService);
    }
    
    @Test
    public void process() throws Exception {
        final DefaultExchange camelExchange = new DefaultExchange(new DefaultCamelContext());
        camelExchange.getIn().setBody("bajja");
        
        final SwitchyardProcessor sp = new SwitchyardProcessor(_serviceName.toString());
        sp.setServiceReference(_registerService);
        sp.process(camelExchange);
        
        assertThat(_mockService.getMessages().size(), is(1));
        assertThat(firstMsgContent(_mockService), is("bajja"));
    }
    
    private String firstMsgContent(final MockHandler handler) {
        return _mockService.getMessages().iterator().next().getMessage().getContent(String.class);
    }

}
