/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.switchyard.quickstarts.demo.txpropagation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.transaction.TransactionMixIn;
import org.switchyard.quickstarts.demo.txpropagation.Application;
import org.switchyard.quickstarts.demo.txpropagation.Car;
import org.switchyard.quickstarts.demo.txpropagation.Deal;
import org.switchyard.quickstarts.demo.txpropagation.Offer;
import org.switchyard.test.Invoker;
import org.switchyard.test.MockHandler;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins = { CDIMixIn.class, TransactionMixIn.class }, config = SwitchYardTestCaseConfig.SWITCHYARD_XML)
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
        testKit.replaceService("DealLogger");
        MockHandler creditService = testKit.replaceService("CreditCheckService");
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
