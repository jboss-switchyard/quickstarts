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

package org.switchyard.admin.base;

import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.deploy.event.ApplicationDeployedEvent;
import org.switchyard.deploy.internal.Deployment;

public class SwitchYardBuilderTest extends SwitchYardBuilderTestBase {

    public SwitchYardBuilderTest() throws Exception {
        super();
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
        SwitchYardBuilder builder = new SwitchYardBuilder();
        builder.notify(new ApplicationDeployedEvent(testDeployment));
        
        Assert.assertEquals(1, builder.getSwitchYard().getApplications().size());
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
