/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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
package org.switchyard.component.camel;

import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.junit.Assert;
import org.junit.Test;
import org.switchyard.component.camel.scanner.ServiceInterface;
import org.switchyard.component.camel.scanner.SingleRouteService;
import org.switchyard.exception.SwitchYardException;

public class RouteFactoryTest {

    @Test
    public void createRouteFromClass() {
        List<RouteDefinition> routes = RouteFactory.createRoute(SingleRouteService.class.getName());

        Assert.assertNotNull(routes);
        Assert.assertEquals(1, routes.size());
        RouteDefinition route = routes.get(0);
        Assert.assertEquals(1, route.getInputs().size());
        Assert.assertEquals(2, route.getOutputs().size());
    }

    @Test
    public void doesntExtendRouteBuilder() {
        try {
            RouteFactory.createRoute(DoesntExtendRouteBuilder.class.getName());
            Assert.fail("Java DSL class does not extend RouteBuilder " + DoesntExtendRouteBuilder.class.getName());
        } catch (RuntimeException ex) {
            System.err.println("Route class that does not extend RouteBuilder was rejected: " + ex.toString());
        }
    }

    @Test
    public void noRoutesDefined() {
        try {
            RouteFactory.createRoute(NoRoutesDefined.class.getName());
            Assert.fail("No routes defined in Java DSL class " + NoRoutesDefined.class.getName());
        } catch (RuntimeException ex) {
            System.err.println("Java DSL class without a route was rejected: " + ex.toString());
        }
    }
    
    @Test
    public void noRoutesDefinedXML() {
        try {
            RouteFactory.loadRoute("org/switchyard/component/camel/deploy/not-a-route.xml");
        } catch (SwitchYardException syEx) {
            System.err.println("Invalid Camel XML definition rejected: " + syEx.toString());
        }
    }
    
    @Test
    public void singleRouteXML() {
        List<RouteDefinition> routes = 
                RouteFactory.loadRoute("org/switchyard/component/camel/deploy/route.xml");
        Assert.assertEquals(1, routes.size());
    }
    
    @Test
    public void multiRouteXML() {
        List<RouteDefinition> routes = 
                RouteFactory.loadRoute("org/switchyard/component/camel/deploy/routes.xml");
        Assert.assertEquals(2, routes.size());
    }
}

class NoRouteAnnotation extends RouteBuilder {
    public void configure() {
        from("direct://foo")
        .to("mock:test");
    }
}

@Route(ServiceInterface.class)
class DoesntExtendRouteBuilder {
    public void configure() {
    }
}

@Route(ServiceInterface.class)
class NoRoutesDefined extends RouteBuilder {
    public void configure() {
    }
}
