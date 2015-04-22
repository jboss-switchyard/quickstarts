/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.karaf.test.quickstarts;

import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.switchyard.quickstarts.remoteinvoker.Application;
import org.switchyard.quickstarts.remoteinvoker.Car;
import org.switchyard.quickstarts.remoteinvoker.Deal;
import org.switchyard.quickstarts.remoteinvoker.Offer;
import org.switchyard.remote.RemoteInvoker;
import org.switchyard.remote.RemoteMessage;
import org.switchyard.remote.http.HttpInvoker;

public class RemoteInvokerQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard.remote.invoker";
    private static String featureName = "switchyard-quickstart-remote-invoker";

    private static final QName SERVICE = new QName("urn:com.example.switchyard:switchyard-quickstart-remote-invoker:1.0", "Dealer");
    private static final String URL = "http://localhost:8181/switchyard-remote";
    
    @BeforeClass
    public static void before() throws Exception {
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
