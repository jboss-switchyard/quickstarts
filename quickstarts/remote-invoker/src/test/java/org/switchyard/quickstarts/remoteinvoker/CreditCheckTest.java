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
package org.switchyard.quickstarts.remoteinvoker;

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
