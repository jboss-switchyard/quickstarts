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

package org.switchyard.component.bean.tests;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.Message;
import org.switchyard.MessageBuilder;
import org.switchyard.ServiceDomain;
import org.switchyard.component.bean.AbstractCDITest;
import org.switchyard.component.bean.BeanServiceMetadata;
import org.switchyard.internal.ServiceDomains;

/*
 * Assorted methods for testing a CDI bean consuming a service in SwitchYard.
 */
public class BeanConsumerTest extends AbstractCDITest {
    
    @Test
    public void consumeInOnlyServiceFromBean() {
        ServiceDomain domain = ServiceDomains.getDomain();

        Exchange exchange = domain.createExchange(new QName("ConsumerBean"), ExchangePattern.IN_ONLY);

        BeanServiceMetadata.setOperationName(exchange, "consumeInOnlyService");
        
        Message inMessage = MessageBuilder.newInstance().buildMessage();
        inMessage.setContent("hello");

        exchange.send(inMessage);
    }
    

    @Test
    public void consumeInOutServiceFromBean() {
        ServiceDomain domain = ServiceDomains.getDomain();

        Exchange exchange = domain.createExchange(new QName("ConsumerBean"), ExchangePattern.IN_ONLY);

        BeanServiceMetadata.setOperationName(exchange, "consumeInOutService");
        
        Message inMessage = MessageBuilder.newInstance().buildMessage();
        inMessage.setContent("hello");

        exchange.send(inMessage);
    }

}
