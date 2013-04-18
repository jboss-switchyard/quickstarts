/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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