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
import org.switchyard.common.type.Classes;

/**
 * Creates RouteDefinition instances based off of a class containing @Route
 * methods and Java DSL route definitions.
 */
public final class RouteFactory {

    /** 
     * Utility class - so no need to directly instantiate.
     */
    private RouteFactory() {
        
    }
    
    /**
     * Create a new route from the given class name and service name.
     * @param className name of the class containing an @Route definition
     * @return the route definition
     */
    public static RouteDefinition createRoute(String className) {
        return createRoute(Classes.forName(className));
    }
    
    /**
     * Create a new route from the given class and service name.
     * @param routeClass class containing an @Route definition
     * @return the route definition
     */
    public static RouteDefinition createRoute(Class<?> routeClass) {
        if (!routeClass.isAnnotationPresent(Route.class)) {
            throw new RuntimeException("@Route definition is missing on class " 
                    + routeClass.getName());
        }
        
        if (!RouteBuilder.class.isAssignableFrom(routeClass)) {
            throw new RuntimeException("Java DSL class " + routeClass.getName() 
                    + " must extend " + RouteBuilder.class.getName());
        }

        // Create the route and tell it to create a route
        RouteBuilder builder;
        RouteDefinition route = null;
        try {
            builder = (RouteBuilder)routeClass.newInstance();
            builder.configure();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to initialize Java DSL class " 
                    + routeClass.getName(), ex);
        }
        
        
        // Make sure the generated route is legit
        List<RouteDefinition> routes = builder.getRouteCollection().getRoutes();
        if (routes.isEmpty()) {
            throw new RuntimeException("No routes found in Java DSL class "
                    + routeClass.getName());
        } else if (routes.size() > 1) {
            throw new RuntimeException("Only one route allowed per Java DSL class "
                    + routeClass.getName());
        }
        route = routes.get(0);
        if (route.getInputs().size() != 1) {
            throw new RuntimeException("Java DSL routes must contain a single 'from' "
                    + routeClass.getName());
        }
        
        return route;
    }
}
