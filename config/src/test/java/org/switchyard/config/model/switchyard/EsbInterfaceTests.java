/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
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
