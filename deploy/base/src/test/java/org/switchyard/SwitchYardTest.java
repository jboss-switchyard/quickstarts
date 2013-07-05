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

package org.switchyard;

import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Test;
import org.switchyard.common.type.Classes;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.deploy.Activator;
import org.switchyard.deploy.BaseActivator;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.deploy.ServiceHandler;
import org.switchyard.standalone.SwitchYard;

public class SwitchYardTest {

    @Test
    public void getActivators() throws Exception {
        InputStream config = Classes.getResourceAsStream("/switchyard-config-mock-01.xml", getClass());
        SwitchYard sy = new SwitchYard(config);
        config.close();

        Assert.assertEquals(1, sy.getActivatorList().size());
    }
    
    @Test
    public void replaceActivator() throws Exception {
        InputStream config = Classes.getResourceAsStream("/switchyard-config-mock-01.xml", getClass());
        SwitchYard sy = new SwitchYard(config);
        
        // Remove the old activator if it exists
        Iterator<Activator> activators = sy.getActivatorList().iterator();
        while (activators.hasNext()) {
            Activator activator = activators.next();
            if (activator.canActivate("mock")) {
                activators.remove();
            }
        }
        
        // Add new activator
        MockActivator mock = new MockActivator();
        sy.getActivatorList().add(mock);
        
        sy.start();
        Assert.assertTrue(mock.activationCalled);
        
        config.close();
    }
    
    @Test
    public void customActivatorList() throws Exception {
        InputStream config = Classes.getResourceAsStream("/switchyard-config-mock-01.xml", getClass());
        SwitchYard sy = new SwitchYard(config);
        
        // Add new activator
        MockActivator mock = new MockActivator();
        List<Activator> activatorList = new LinkedList<Activator>();
        activatorList.add(mock);
        sy.setActivatorList(activatorList);
        
        sy.start();
        Assert.assertTrue(mock.activationCalled);
        
        config.close();
    }
}

class MockActivator extends BaseActivator {
    boolean activationCalled;
    
    public MockActivator() {
        super("mock");
    }
    
    @Override
    public ServiceHandler activateService(QName name, ComponentModel config) {
        activationCalled = true;
        return new BaseServiceHandler();
    }
    
    @Override
    public ServiceHandler activateBinding(QName name, BindingModel config) {
        return new BaseServiceHandler();
    }
}
