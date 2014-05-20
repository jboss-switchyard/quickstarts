package org.switchyard.karaf.test.quickstarts;

import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.quickstarts.remoteinvoker.Application;
import org.switchyard.quickstarts.remoteinvoker.Car;
import org.switchyard.quickstarts.remoteinvoker.Deal;
import org.switchyard.quickstarts.remoteinvoker.Offer;
import org.switchyard.remote.RemoteInvoker;
import org.switchyard.remote.RemoteMessage;
import org.switchyard.remote.http.HttpInvoker;

public class RemoteInvokerQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard-remote-invoker";
    private static String featureName = "switchyard-quickstart-remote-invoker";

    private static final QName SERVICE = new QName("urn:com.example.switchyard:switchyard-quickstart-remote-invoker:1.0", "Dealer");
    private static final String URL = "http://localhost:8080/switchyard-remote";
    
    @Before
    public void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }

    @Test
    public void testOffer() throws Exception {
        
        RemoteInvoker invoker = new HttpInvoker(URL);
        Application app = new Application();
        app.setName("Magesh");
        app.setCreditScore(812);
        Car car = new Car();
        car.setPrice(9600);
        Offer offer = new Offer();
        offer.setApplication(app);
        offer.setCar(car);
        offer.setAmount(9000);

        RemoteMessage message = new RemoteMessage();
        message.setService(SERVICE)
            .setOperation("offer")
            .setContent(offer);

        // Invoke the service
        RemoteMessage reply = invoker.invoke(message);
        Deal deal = (Deal)reply.getContent();
        Assert.assertTrue(deal.isAccepted());
    }
}
