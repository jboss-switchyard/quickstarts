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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.test.Invoker;
import org.switchyard.test.MockHandler;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins = CDIMixIn.class, config = SwitchYardTestCaseConfig.SWITCHYARD_XML)
public class DealerTest {

    @ServiceOperation("Dealer")
    private Invoker service;
    
    private SwitchYardTestKit testKit;
    
    @Before
    public void setUp() {
        
    }

    @Test
    public void rejectLowBallOffer() throws Exception {
        Car car = new Car();
        car.setPrice(500.00);
        Offer offer = new Offer();
        offer.setCar(car);
        offer.setAmount(100.00);

        Deal deal = service.operation("offer").sendInOut(offer).getContent(Deal.class);

        // verify the deal is rejected
        Assert.assertFalse(deal.isAccepted());
    }

    @Test
    public void acceptValidOffer() throws Exception {
        Car car = new Car();
        car.setPrice(500.00);
        Application app = new Application();
        app.setCreditScore(650);
        Offer offer = new Offer();
        offer.setCar(car);
        offer.setApplication(app);
        offer.setAmount(450.00);
        
        // configure our proxy for the service reference
        MockHandler creditService = testKit.replaceService("CreditCheck");
        Application reply = new Application();
        reply.setApproved(true);
        creditService.replyWithOut(reply);

        // Invoke the service
        Deal deal = service.operation("offer").sendInOut(offer)
                .getContent(Deal.class);

        // verify the deal is rejected
        Assert.assertTrue(deal.isAccepted());
    }

}
