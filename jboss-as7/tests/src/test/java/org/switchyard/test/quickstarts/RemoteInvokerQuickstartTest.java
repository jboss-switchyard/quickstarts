/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
        return ArquillianUtil.createJarQSDeployment("switchyard-quickstart-remote-invoker");
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
