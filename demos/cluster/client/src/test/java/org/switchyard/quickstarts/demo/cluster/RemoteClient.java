/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.quickstarts.demo.cluster;

import static java.lang.System.out;
import javax.xml.namespace.QName;

import org.switchyard.remote.RemoteInvoker;
import org.switchyard.remote.RemoteMessage;
import org.switchyard.remote.http.HttpInvoker;

/**
 * Test client which uses RemoteInvoker to invoke a service with an SCA binding.
 */
public final class RemoteClient {

    private static final QName SERVICE = new QName(
            "urn:switchyard-quickstart-demo-cluster-dealer:1.0",
            "Dealer");
    private static final String URL = "http://localhost:8080/switchyard-remote";

    /**
     * Private no-args constructor.
     */
    private RemoteClient() {
    }

    /**
     * Only execution point for this application.
     * @param ignored not used.
     * @throws Exception if something goes wrong.
     */
    public static void main(final String[] ignored) throws Exception {
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
            System.err.println("Oops ... something bad happened.  "
                    + reply.getContent());
        } else {
            Deal deal = (Deal) reply.getContent();
            out.println("==================================");
            out.println("Was the offer accepted? " + deal.isAccepted());
            out.println("==================================");
        }
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
