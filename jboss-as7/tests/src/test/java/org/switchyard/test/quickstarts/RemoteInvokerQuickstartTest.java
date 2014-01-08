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
package org.switchyard.test.quickstarts;

import javax.xml.namespace.QName;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.quickstarts.remoteinvoker.Application;
import org.switchyard.quickstarts.remoteinvoker.Car;
import org.switchyard.quickstarts.remoteinvoker.Deal;
import org.switchyard.quickstarts.remoteinvoker.Offer;
import org.switchyard.remote.RemoteInvoker;
import org.switchyard.remote.RemoteMessage;
import org.switchyard.remote.http.HttpInvoker;
import org.switchyard.test.ArquillianUtil;

@RunWith(Arquillian.class)
public class RemoteInvokerQuickstartTest {
    
    private static final QName SERVICE = 
    			new QName("urn:com.example.switchyard:switchyard-quickstart-remote-invoker:1.0", "Dealer");
    private static final String URL = "http://localhost:8080/switchyard-remote";

    @Deployment(testable = false)
    public static JavaArchive createDeployment() {
        return ArquillianUtil.createJarQSDeployment("switchyard-remote-invoker");
    }

    @Test
    public void testOffer() throws Exception {
        
        RemoteInvoker invoker = new HttpInvoker(URL);
        Application app = new Application();
        app.setCreditScore(700);
        Car car = new Car();
        car.setPrice(18000);
        Offer offer = new Offer();
        offer.setApplication(app);
        offer.setCar(car);
        offer.setAmount(17000);

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
