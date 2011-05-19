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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.camel.model.ProcessorDefinition;
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
     * @param serviceName name of the service which must match the value of the
     *        @Route annotation
     * @return the route definition
     */
    public static RouteDefinition createRoute(String className, QName serviceName) {
        return createRoute(Classes.forName(className), serviceName);
    }
    
    /**
     * Create a new route from the given class and service name.
     * @param routeClass class containing an @Route definition
     * @param serviceName name of the service which must match the value of the
     *        @Route annotation
     * @return the route definition
     */
    public static RouteDefinition createRoute(Class<?> routeClass, QName serviceName) {
        Method routeMethod = getRouteMethod(routeClass, serviceName);
        if (routeMethod == null) {
            throw new RuntimeException("No route definition found for service " 
                    + serviceName + " on class " + routeClass.getName());
        }
        
        // verify route method signature
        if (!Modifier.isPublic(routeMethod.getModifiers())) {
            throw new RuntimeException("@Route method must be public: " + routeMethod.getName());
        }
        Class<?>[] params = routeMethod.getParameterTypes();
        if (params.length != 1 || !ProcessorDefinition.class.isAssignableFrom(params[0])) {
            throw new RuntimeException(
                    "@Route methods must have a single parameter of type ProcessorDefinition: "
                    + routeMethod.getName());
        }
        
        RouteDefinition route = new RouteDefinition(); 
        try {
            routeMethod.invoke(routeClass.newInstance(), route);
        } catch (Exception ex) {
            throw new RuntimeException("Error while invoking route builder method on " 
                    + routeClass.getName(), ex);
        }
        
        return route;
    }
    
    /**
     * Returns a list of methods annotated with @Route inside the specified class.
     * @param clazz class to scan
     * @return list of methods in the class annotated with @Route
     */
    public static List<Method> getRouteMethods(Class<?> clazz) {
        List<Method> methods = new LinkedList<Method>();
        for (Method publicMethod : clazz.getDeclaredMethods()) {
            if (publicMethod.isAnnotationPresent(Route.class)) {
                methods.add(publicMethod);
            }
        }
        return methods;
    }
    
    private static Method getRouteMethod(Class<?> clazz, QName serviceName) {
        Method method = null;
        for (Method routeMethod : getRouteMethods(clazz)) {
            Route route = routeMethod.getAnnotation(Route.class);
            if (route.value().getSimpleName().equals(serviceName.getLocalPart())) {
                method = routeMethod;
                break;
            }
        }
        return method;
    }
}
