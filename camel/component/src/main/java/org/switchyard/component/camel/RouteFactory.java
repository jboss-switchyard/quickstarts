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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.Constants;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import org.switchyard.common.type.Classes;
import org.switchyard.component.camel.model.CamelComponentImplementationModel;
import org.switchyard.SwitchYardException;

/**
 * Creates RouteDefinition instances based off of a class containing @Route
 * methods and Java DSL route definitions.
 */
public final class RouteFactory {

    /**
     * JAXB context for reading XML definitions.
     */
    private static JAXBContext JAXB_CONTEXT;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(Constants.JAXB_CONTEXT_PACKAGES, CamelContext.class.getClassLoader());
        } catch (JAXBException e) {
            throw new SwitchYardException(e);
        }
    }

    /** 
     * Utility class - so no need to directly instantiate.
     */
    private RouteFactory() {
        
    }

    /**
     * Returns a list of route definitions referenced by a camel implementation.
     * @param model implementation config model
     * @return list of route definitions
     */
    public static List<RouteDefinition> getRoutes(CamelComponentImplementationModel model) {
        if (model.getJavaClass() != null) {
            return createRoute(model.getJavaClass(), model.getComponent().getTargetNamespace());
        }
        return loadRoute(model.getXMLPath());
    }

    /**
     * Loads a set of route definitions from an XML file.
     * @param xmlPath path to the file containing one or more route definitions
     * @return list of route definitions
     */
    public static List<RouteDefinition> loadRoute(String xmlPath) {
        List<RouteDefinition> routes = null;
        
        try {
            Source source =  new StreamSource(Classes.getResourceAsStream(xmlPath));
            Object obj = JAXB_CONTEXT.createUnmarshaller().unmarshal(source);
            
            // Look for <routes> or <route> as top-level element
            if (obj instanceof RoutesDefinition) {
                routes = ((RoutesDefinition)obj).getRoutes();
            } else if (obj instanceof RouteDefinition) {
                routes = new ArrayList<RouteDefinition>(1);
                routes.add((RouteDefinition)obj);
            }
            
            // If we couldn't find a <route> or <routes> definition, throw an error
            if (routes == null) {
                throw new SwitchYardException("No routes found in XML file " + xmlPath);
            }
            return routes;
        } catch (JAXBException e) {
            throw new SwitchYardException(e);
        } catch (IOException e) {
            throw new SwitchYardException(e);
        }
    }

    /**
     * Create a new route from the given class name and service name.
     * @param className name of the class containing an @Route definition
     * @return the route definition
     */
    public static List<RouteDefinition> createRoute(String className) {
        return createRoute(className, null);
    }

    /**
     * Create a new route from the given class name and service name.
     * @param className name of the class containing an @Route definition
     * @param namespace the namespace to append to switchyard:// service URIs
     * @return the route definition
     */
    public static List<RouteDefinition> createRoute(String className, String namespace) {
        return createRoute(Classes.forName(className), namespace);
    }

    /**
     * Create a new route from the given class and service name.
     * @param routeClass class containing an @Route definition
     * @return the route definition
     */
    public static List<RouteDefinition> createRoute(Class<?> routeClass) {
        return createRoute(routeClass, null);
    }

    /**
     * Create a new route from the given class and service name.
     * @param routeClass class containing an @Route definition
     * @param namespace the namespace to append to switchyard:// service URIs
     * @return the route definition
     */
    public static List<RouteDefinition> createRoute(Class<?> routeClass, String namespace) {
        if (!RouteBuilder.class.isAssignableFrom(routeClass)) {
            throw new SwitchYardException("Java DSL class " + routeClass.getName() 
                + " must extend " + RouteBuilder.class.getName());
        }

        // Create the route and tell it to create a route
        RouteBuilder builder;
        try {
            builder = (RouteBuilder) routeClass.newInstance();
            builder.configure();
            List<RouteDefinition> routes = builder.getRouteCollection().getRoutes();
            if (routes.isEmpty()) {
                throw new SwitchYardException("No routes found in Java DSL class " + routeClass.getName());
            }
            return routes;
        } catch (Exception ex) {
            throw new SwitchYardException("Failed to initialize Java DSL class " 
                    + routeClass.getName(), ex);
        }
    }

}
