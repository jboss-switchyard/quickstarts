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
package org.switchyard.component.camel;

import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.junit.Assert;
import org.junit.Test;
import org.switchyard.component.camel.scanner.ServiceInterface;
import org.switchyard.component.camel.scanner.SingleRouteService;
import org.switchyard.SwitchYardException;

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
