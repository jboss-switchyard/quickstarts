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
