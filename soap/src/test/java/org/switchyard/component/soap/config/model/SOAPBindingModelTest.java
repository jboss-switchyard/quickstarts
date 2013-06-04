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
package org.switchyard.component.soap.config.model;

import junit.framework.Assert;

import org.junit.Test;
import org.switchyard.config.model.ModelPuller;

/**
 * Test of SOAP binding model.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class SOAPBindingModelTest {

    private static final String SOAP_BINDING = "soap-binding-reference.xml";

    @Test
    public void testReadConfigBinding() throws Exception {
        ModelPuller<SOAPBindingModel> puller = new ModelPuller<SOAPBindingModel>();
        SOAPBindingModel binding = puller.pull(SOAP_BINDING, getClass());
        binding.assertModelValid();
        Assert.assertEquals("http://modified.com/phantom", binding.getEndpointAddress());
        EndpointConfigModel endpointConfig = binding.getEndpointConfig();
        Assert.assertEquals("myFile", endpointConfig.getConfigFile());
        Assert.assertEquals("myName", endpointConfig.getConfigName());
        MtomModel mtomConfig = binding.getMtomConfig();
        Assert.assertEquals(new Boolean(true), mtomConfig.isEnabled());
        Assert.assertEquals(new Integer(1300), mtomConfig.getThreshold());
        Assert.assertEquals(new Boolean(true), mtomConfig.isXopExpand());
    }
}
