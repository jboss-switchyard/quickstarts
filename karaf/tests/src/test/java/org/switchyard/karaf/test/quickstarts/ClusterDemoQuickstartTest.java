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

import static java.lang.System.out;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;

import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ops4j.pax.exam.CoreOptions;
import org.switchyard.quickstarts.remoteinvoker.Application;
import org.switchyard.quickstarts.remoteinvoker.Car;
import org.switchyard.quickstarts.remoteinvoker.Deal;
import org.switchyard.quickstarts.remoteinvoker.Offer;
import org.switchyard.remote.RemoteInvoker;
import org.switchyard.remote.RemoteMessage;
import org.switchyard.remote.http.HttpInvoker;

public class ClusterDemoQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.demos.switchyard-demo-cluster-dealer";
    private static String featureName = "switchyard-demo-cluster-dealer";
    private static String[] extraFeatures = {"switchyard-demo-cluster-credit"};

    private static final QName SERVICE = new QName(
            "urn:switchyard-quickstart-demo-cluster-dealer:1.0",
            "Dealer");
    private static final String URL = "http://localhost:8181/switchyard-remote";
    
    @BeforeClass
    public static void before() throws Exception {
        startTestContainer(featureName, bundleName, 
                CoreOptions.options(features(
                        "mvn:org.switchyard.karaf/switchyard/2.0.0-SNAPSHOT/xml/features", 
                        extraFeatures)));
    }

    @Test
    public void testOffer() throws Exception {
        
     // Create a new remote client invoker
        RemoteInvoker invoker = new HttpInvoker(URL);

        // Create request payload
        Offer offer = createOffer(true);

        // Create the request message
        RemoteMessage message = new RemoteMessage();
        message.setService(SERVICE).setOperation("offer").setContent(offer);

        // Invoke the service
        RemoteMessage reply = invoker.invoke(message);
        if (reply.isFault()) {
            Assert.fail("Invocation returned fault: " + reply.getContent());
        }
        Deal deal = (Deal)reply.getContent();
        Assert.assertTrue(deal.isAccepted());
    }

    public static Offer createOffer(boolean acceptable) {
        Application app = new Application();
        app.setName("John Smith");
        app.setCreditScore(acceptable ? 700 : 300);
        Car car = new Car();
        car.setPrice(18000);
        car.setVehicleId("Honda");
        Offer offer = new Offer();
        offer.setApplication(app);
        offer.setCar(car);
        offer.setAmount(17000);

        return offer;
    }
}
