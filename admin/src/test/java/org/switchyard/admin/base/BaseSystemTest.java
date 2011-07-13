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

package org.switchyard.admin.base;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.admin.Application;
import org.switchyard.admin.Component;
import org.switchyard.admin.ComponentType;
import org.switchyard.admin.Service;

public class BaseSystemTest {
    
    private BaseSwitchYard _switchYard;
    
    @Before
    public void setUp() {
        _switchYard = new BaseSwitchYard("1.0");
    }
    
    @Test
    public void testApplication() {
        Application app1 = new BaseApplication(new QName("app1"), null);
        Application app2 = new BaseApplication(new QName("app2"), null);
        
        _switchYard.addApplication(app1);
        _switchYard.addApplication(app2);
        Assert.assertEquals(2, _switchYard.getApplications().size());
        
        _switchYard.removeApplication(app1);
        Assert.assertEquals(app2, _switchYard.getApplications().get(0));
    }
    

    @Test
    public void testComponent() {
        Component c1 = new BaseComponent("c1", ComponentType.GATEWAY, null);
        Component c2 = new BaseComponent("c2", ComponentType.IMPLEMENTATION, null);
        
        _switchYard.addComponent(c1);
        _switchYard.addComponent(c2);
        Assert.assertEquals(2, _switchYard.getComponents().size());
        
        _switchYard.removeComponent(c1);
        Assert.assertEquals(c2, _switchYard.getComponents().get(0));
    }
    
    @Test
    public void testService() {
        Service s1 = new BaseService(new QName("s1"), null, null, null, null);
        Service s2 = new BaseService(new QName("s2"), null, null, null, null);
        
        _switchYard.addService(s1);
        _switchYard.addService(s2);
        Assert.assertEquals(2, _switchYard.getServices().size());
        
        _switchYard.removeService(s1);
        Assert.assertEquals(s2, _switchYard.getServices().get(0));
    }
}
