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

package org.switchyard.config.model.switchyard;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.composite.ComponentModel;

/**
 * Test interface.esb configuration in composite model.
 */
public class EsbInterfaceTests {
	
    private static final String ESB_INTERFACE_XML = 
    		"/org/switchyard/config/model/switchyard/EsbInterfaceTests.xml";
    
    private SwitchYardModel _model;

    @Before
    public void before() throws Exception {
    	ModelPuller<SwitchYardModel> puller = new ModelPuller<SwitchYardModel>();
    	_model = puller.pull(ESB_INTERFACE_XML, getClass());
    	Assert.assertTrue(_model.isModelValid());
    }

    @Test
    public void testServiceInterface() {
        ComponentModel comp = _model.getComposite().getComponents().get(0);
        EsbInterfaceModel serviceInterface = (EsbInterfaceModel)comp.getServices().get(0).getInterface();
        Assert.assertNotNull(serviceInterface);
        Assert.assertEquals(EsbInterfaceModel.ESB, serviceInterface.getType());
    }

    @Test
    public void testReferenceInterface() {
        ComponentModel comp = _model.getComposite().getComponents().get(0);
        EsbInterfaceModel referenceInterface = (EsbInterfaceModel)comp.getReferences().get(0).getInterface();
        Assert.assertNotNull(referenceInterface);
        Assert.assertEquals(EsbInterfaceModel.ESB, referenceInterface.getType());
        Assert.assertEquals(new QName("urn:123", "abc"), referenceInterface.getInputType());
        Assert.assertEquals("xyz", referenceInterface.getOutputType().toString());
        Assert.assertEquals("omg", referenceInterface.getFaultType().toString());
    }
}
