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

import javax.inject.Inject;

import org.junit.Assert;
import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;

@Service
public class ConsumerBean {
    
    @Inject @Reference
    private OneWay oneWay;
    
    @Inject @Reference
    private RequestResponse requestResponse;
    
    public void consumeInOnlyService(Object message) {
        oneWay.oneWay(message);
    }
    
    public Object consumeInOutService(Object message) throws ConsumerException {
        try {
            Object reply = null;
            reply = requestResponse.reply(message);
            Assert.assertEquals(message, reply);
            return reply;
        } catch (ConsumerException e) {
            Assert.assertEquals(message, e);
            // OK... this validates that the remote exception was transported through the
            // Exchange fault mechanism and then rethrown by the client proxy.  Create
            // and throw a new exception...
            throw new ConsumerException("remote-exception-received");
        }
    }
}
