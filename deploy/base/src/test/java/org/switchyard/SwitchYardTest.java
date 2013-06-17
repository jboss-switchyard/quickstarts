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
