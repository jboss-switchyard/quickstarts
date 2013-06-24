/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.composite.ExtensionsModel;
import org.switchyard.config.model.switchyard.v1.V1ThrottlingModel;

/**
 * ThrottlingExtensionTests.
 */
public class ThrottlingExtensionTests {

    private static final String THROTTLING_XML = "/org/switchyard/config/model/switchyard/ThrottlingExtensionTests.xml";
    private static final Integer MAX_REQUESTS = 50;
    private static final Long TIME_PERIOD = 2000L;

    private ModelPuller<SwitchYardModel> _puller;

    @Before
    public void before() throws Exception {
        _puller = new ModelPuller<SwitchYardModel>();
    }

    @Test
    public void testCreate() throws Exception {
        ThrottlingModel throttling = new V1ThrottlingModel();
        throttling.setMaxRequests(MAX_REQUESTS).setTimePeriod(TIME_PERIOD);

        Assert.assertEquals(MAX_REQUESTS, (Integer) throttling.getMaxRequests());
        Assert.assertEquals(TIME_PERIOD, throttling.getTimePeriod());
    }

    @Test
    public void testRead() throws Exception {
        SwitchYardModel switchyard = _puller.pull(THROTTLING_XML, getClass());
        ExtensionsModel extensions = switchyard.getComposite().getServices().get(0).getExtensions();
        ThrottlingModel throttling = extensions.getThrottling();

        Assert.assertEquals(MAX_REQUESTS, (Integer) throttling.getMaxRequests());
        Assert.assertEquals(TIME_PERIOD, throttling.getTimePeriod());
    }

    @Test
    public void testValidation() throws Exception {
        SwitchYardModel switchyard = _puller.pull(THROTTLING_XML, getClass());
        switchyard.assertModelValid();
    }

}
