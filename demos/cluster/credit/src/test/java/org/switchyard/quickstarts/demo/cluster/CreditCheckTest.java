/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.quickstarts.demo.cluster;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins = CDIMixIn.class, config = SwitchYardTestCaseConfig.SWITCHYARD_XML)
public class CreditCheckTest {

    @ServiceOperation("CreditCheck")
    private Invoker service;

    // Verify that credit scores at or above 600 are approved
    @Test
    public void creditApproved() throws Exception {
        Application app = new Application();
        app.setName("phil garfield");
        app.setCreditScore(600);
        Offer offer = new Offer();
        offer.setApplication(app);

        Application result = service.operation("checkCredit").sendInOut(offer).getContent(Application.class);

        // validate the results
        Assert.assertTrue(result.isApproved());
    }

    // Verify that credit scores below 600 are denied
    @Test
    public void creditDenied() throws Exception {
        Application app = new Application();
        app.setName("bill whatwhatwhat");
        app.setCreditScore(400);
        Offer offer = new Offer();
        offer.setApplication(app);

        Application result = service.operation("checkCredit").sendInOut(offer).getContent(Application.class);

        // validate the results
        Assert.assertFalse(result.isApproved());
    }
}
