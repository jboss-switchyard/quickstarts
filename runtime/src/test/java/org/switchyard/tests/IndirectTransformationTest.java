/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.tests;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.MockDomain;
import org.switchyard.MockHandler;
import org.switchyard.ServiceReference;
import org.switchyard.handlers.TransformHandler;
import org.switchyard.transform.BaseTransformer;
import org.switchyard.transform.TransformSequence;
import org.switchyard.transform.Transformer;

/**
 * Tests for exercising the TransformationHandler during message exchange.
 */
public class IndirectTransformationTest {
    
    private MockDomain _domain;

    @Before
    public void setUp() throws Exception {
        _domain = new MockDomain();
    }

    /* Tests to Add :
     * - test one level deep transformation
     */

    /**
     * Test that the secondary Transform that is one level deep (i.e. A->B, B-C) is applied if the primary Transform (A->C) is not available
     */
    @Test
    public void testOneLevelDeepTransformInProvider() throws Exception {
        final QName serviceName = new QName("nameTransform");
        final QName inType = new QName("fromA");
        final QName indirectDestOutType = new QName("viaB");
        final QName indirectDestInType = new  QName("viaB");
        final QName finalDestOutType = new  QName("toC");
        
        final String input = "Hello";
        
        // Add transforms to registry
        Transformer<String, String> transformAtoB = 
                new BaseTransformer<String, String>(inType, indirectDestOutType) {
            public String transform(String from) {
                // transform the input date to the desired output string
                return from + " there,";
            }
        };
        _domain.getTransformerRegistry().addTransformer(transformAtoB);
        
        Transformer<String, String> transformBtoC = 
                new BaseTransformer<String, String>(indirectDestInType, finalDestOutType) {
            public String transform(String from) {
                // transform the input date to the desired output string
                return from + " SwitchYard";
            }
        };
        _domain.getTransformerRegistry().addTransformer(transformBtoC);
        
        try {
            // Provide the service
            TestTransformHandler serviceHandler = new TestTransformHandler();
            ServiceReference service = _domain.createInOnlyService(serviceName, serviceHandler);
        
            // Create the exchange and invoke the service
            MockHandler invokerHandler = new MockHandler();
            Exchange exchange = service.createExchange(invokerHandler);
        
            // Set the from and to message names.  NOTE: setting to the to message
            // name will not be necessary once the service definition is available
            // at runtime
            Message msg = exchange.createMessage().setContent(input);
            TransformSequence.
                    from(inType).
                    to(finalDestOutType).
                    associateWith(msg);
        
            msg.setContent(input);
            
            exchange.send(msg);

            Assert.assertTrue(serviceHandler.isSuccess());
        } finally {
            // Must remove this transformer, otherwise it's there for the following test... will be
            // fixed once we get rid of the static service domain.
            _domain.getTransformerRegistry().removeTransformer(transformAtoB);
            _domain.getTransformerRegistry().removeTransformer(transformBtoC);
        }        
    }
    
    private class TestTransformHandler extends TransformHandler {
        boolean success = false;
        
        public boolean isSuccess() {
            return success;
        }
        
        @Override
        public void handleMessage(Exchange exchange) throws HandlerException {
            String content = (String) exchange.getMessage().getContent();
            if (content.equals("Hello there, SwitchYard")) {
                success=true;
            }
        }
    }
}
