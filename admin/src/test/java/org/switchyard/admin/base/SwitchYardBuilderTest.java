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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.deploy.event.ApplicationDeployedEvent;
import org.switchyard.deploy.internal.Deployment;

public class SwitchYardBuilderTest {

    private static final QName TEST_APP = new QName("test-app");
    
    private BaseSwitchYard _switchYard;
    private Deployment _deployment;
    
    public SwitchYardBuilderTest() throws Exception {
        _deployment = new MockDeployment(
                new ModelPuller<SwitchYardModel>().pull("switchyard.xml", getClass()), 
                TEST_APP);
    }
    
    @Before
    public void setUp() {
        _switchYard = new BaseSwitchYard("1.0");
        SwitchYardBuilder builder = new SwitchYardBuilder(_switchYard);
        builder.notify(new ApplicationDeployedEvent(_deployment));
    }
    
    @Test
    public void testApplication() {
        Assert.assertEquals(1, _switchYard.getApplications().size());
    }
    
    @Test
    public void testService() {
        Assert.assertEquals(1, _switchYard.getApplication(TEST_APP).getServices().size());
    }

    @Test
    public void testComponent() {
        Assert.assertEquals(2, _switchYard.getApplication(TEST_APP).getComponentServices().size());
    }

    @Test
    public void testNoComponentService() throws Exception{
        Deployment testDeployment = new MockDeployment(
                new ModelPuller<SwitchYardModel>().pull("switchyard_multiappweb.xml", getClass()), 
                QName.valueOf("{urn:switchyard-quickstart-demo:multiapp:0.1.0}web"));
        BaseSwitchYard switchYard = new BaseSwitchYard("1.0");
        SwitchYardBuilder builder = new SwitchYardBuilder(switchYard);
        builder.notify(new ApplicationDeployedEvent(testDeployment));
        
        Assert.assertEquals(1, switchYard.getApplications().size());
    }

}

class MockDeployment extends Deployment {
    private QName _name;
    
    MockDeployment(SwitchYardModel config, QName name) {
        super(config);
        _name = name;
    }
    
    @Override
    public QName getName() {
        return _name;
    }
}
