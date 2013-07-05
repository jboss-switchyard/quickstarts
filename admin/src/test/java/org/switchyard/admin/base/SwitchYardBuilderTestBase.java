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

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.switchyard.admin.SwitchYard;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.deploy.event.ApplicationDeployedEvent;
import org.switchyard.deploy.event.ApplicationUndeployedEvent;
import org.switchyard.deploy.internal.Deployment;

/**
 * Base class for builder tests.
 */
@Ignore
public class SwitchYardBuilderTestBase {

    protected static final QName TEST_APP = new QName("test-app");
    protected SwitchYard _switchYard;
    protected Deployment _deployment;
    protected SwitchYardBuilder _builder;

    public SwitchYardBuilderTestBase() throws Exception {
        _deployment = new MockDeployment(new ModelPuller<SwitchYardModel>().pull("switchyard.xml", getClass()), 
            TEST_APP);
    }

    @Before
    public void setUp() throws Exception {
        _builder = new SwitchYardBuilder();
        _switchYard = _builder.getSwitchYard();
        _builder.notify(new ApplicationDeployedEvent(_deployment));
        //Thread.sleep(300 * 1000);
    }
    
    @After
    public void tearDown() {
        _builder.notify(new ApplicationUndeployedEvent(_deployment));
    }

}