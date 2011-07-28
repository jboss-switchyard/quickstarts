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


package org.switchyard.component.camel.config.model;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.junit.Assert;
import org.junit.Test;
import org.switchyard.component.camel.Route;
import org.switchyard.component.camel.RouteFactory;

public class RouteFactoryTest {

    @Test
    public void createRouteFromClass() {
        RouteDefinition route = RouteFactory.createRoute(SingleRouteService.class.getName());
        
        Assert.assertNotNull(route);
        Assert.assertEquals(1, route.getInputs().size());
        Assert.assertEquals(2, route.getOutputs().size());
    }
    
    @Test
    public void noRouteAnnotation() {
        try {
            RouteFactory.createRoute(NoRouteAnnotation.class.getName());
            Assert.fail("No route annotation on " + NoRouteAnnotation.class.getName());
        } catch (RuntimeException ex) {
            System.err.println("Route without annotation was rejected: " + ex.toString());
        }
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
