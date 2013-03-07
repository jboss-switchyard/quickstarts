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
public class DealerTest {

    @ServiceOperation("Dealer")
    private Invoker service;

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

        Deal deal = service.operation("offer").sendInOut(offer)
                .getContent(Deal.class);

        // verify the deal is rejected
        Assert.assertTrue(deal.isAccepted());
    }

}
