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

import java.lang.reflect.Method;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.junit.Assert;
import org.junit.Test;
import org.switchyard.component.camel.Route;
import org.switchyard.component.camel.RouteFactory;

public class RouteFactoryTest {

    @Test
    public void createRouteFromClass() {
        RouteDefinition route = RouteFactory.createRoute(
                SingleRouteService.class.getName(), 
                QName.valueOf(ServiceInterface.class.getSimpleName()));
        
        Assert.assertNotNull(route);
        Assert.assertTrue(route.getInputs().isEmpty());
        Assert.assertEquals(2, route.getOutputs().size());
    }
    
    @Test
    public void getRouteMethods() {
        List<Method> routeMethods = RouteFactory.getRouteMethods(SingleRouteService.class);
        Assert.assertEquals(1, routeMethods.size());
    }
    
    @Test
    public void nonPublicRouteMethod() {
        try {
            RouteFactory.createRoute(
                    NonPublicRoute.class.getName(), 
                    QName.valueOf(ServiceInterface.class.getSimpleName()));
            Assert.fail("Non public routes are bad!");
        } catch (RuntimeException ex) {
            System.out.println("Non-public route method was rejected: " + ex.toString());
        }
    }
    
    @Test
    public void tooManyParametersOnRouteMethod() {
        try {
            RouteFactory.createRoute(
                    TooManyParametersRoute.class.getName(), 
                    QName.valueOf(ServiceInterface.class.getSimpleName()));
            Assert.fail("Only one parameter for a route!");
        } catch (RuntimeException ex) {
            System.out.println("Route method with too many params was rejected: " + ex.toString());
        }
    }

    @Test
    public void notEnoughParametersOnRouteMethod() {
        try {
            RouteFactory.createRoute(
                    NotEnoughParametersRoute.class.getName(), 
                    QName.valueOf(ServiceInterface.class.getSimpleName()));
            Assert.fail("Need one parameter for route methods!");
        } catch (RuntimeException ex) {
            System.out.println("Route method with no params was rejected: " + ex.toString());
        }
    }

    @Test
    public void wrongTypeOnRouteMethod() {
        try {
            RouteFactory.createRoute(
                    WrongTypeRoute.class.getName(), 
                    QName.valueOf(ServiceInterface.class.getSimpleName()));
            Assert.fail("Route paramter must be ProcessorDefinition!");
        } catch (RuntimeException ex) {
            System.out.println("Route method with wrong type was rejected: " + ex.toString());
        }
    }
    

    @Test
    public void badServiceOnRouteMethod() {
        try {
            RouteFactory.createRoute(
                    WrongInterfaceOnRoute.class.getName(), 
                    QName.valueOf(ServiceInterface.class.getSimpleName()));
            Assert.fail("Service interface didn't match - this should be an error!");
        } catch (RuntimeException ex) {
            System.out.println("Mismatched service interface on route method was rejected: " + ex.toString());
        }
    }
}

class NonPublicRoute {
    @Route(ServiceInterface.class)
    void define(ProcessorDefinition<RouteDefinition> route) {
        
    }
}


class WrongInterfaceOnRoute {
    @Route(String.class)
    public void define(ProcessorDefinition<RouteDefinition> route) {
        
    }
}


class TooManyParametersRoute {
    @Route(ServiceInterface.class)
    public void define(ProcessorDefinition<RouteDefinition> route, String extra) {
        
    }
}


class NotEnoughParametersRoute {
    @Route(ServiceInterface.class)
    public void define() {
        
    }
}


class WrongTypeRoute {
    @Route(ServiceInterface.class)
    public void define(String route) {
        
    }
}