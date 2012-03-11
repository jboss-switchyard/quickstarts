/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */

package org.switchyard.component.camel.processor;

import org.apache.camel.Processor;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for Camel processor factory.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class ProcessorFactoryTest {

    @Test
    public void createProcessors() {
        Processor processor = ProcessorFactory.newProcessor(null, null, "cxfrs://http:///warehouse?resourceClasses=org.switchyard.component.camel.processor.support.WarehouseResource");
        Assert.assertTrue(processor instanceof CxfRsHttpDynamicProcessor);
        processor = ProcessorFactory.newProcessor(null, null, "cxfrs://http://localhost:8080/warehouse?resourceClasses=org.switchyard.component.camel.processor.support.WarehouseResource");
        Assert.assertTrue(processor instanceof CxfRsHttpDynamicProcessor);
        processor = ProcessorFactory.newProcessor(null, null, "cxfrs://http://localhost:8080/warehouse/add?name=test&itemId=1");
        Assert.assertTrue(processor instanceof CxfRsHttpProcessor);
        processor = ProcessorFactory.newProcessor(null, null, "cxfrs://http:///warehouse/remove?itemId=1");
        Assert.assertTrue(processor instanceof CxfRsHttpProcessor);
        processor = ProcessorFactory.newProcessor(null, null, "vm://blah/blah");
        Assert.assertTrue(processor instanceof DefaultProcessor);
        processor = ProcessorFactory.newProcessor(null, null, "file://blah/blah");
        Assert.assertTrue(processor instanceof DefaultProcessor);
        processor = ProcessorFactory.newProcessor(null, null, "jms://blah/blah");
        Assert.assertTrue(processor instanceof DefaultProcessor);
        processor = ProcessorFactory.newProcessor(null, null, "direct://blah/blah");
        Assert.assertTrue(processor instanceof DefaultProcessor);
        try {
            processor = ProcessorFactory.newProcessor(null, null, "cxfrs://http:///warehouse?resourceClasses=org.switchyard.component.camel.processor.support.OrderResource");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof RuntimeException);
            Assert.assertEquals("Unable to load class org.switchyard.component.camel.processor.support.OrderResource", e.getMessage());
        }
    }
}
