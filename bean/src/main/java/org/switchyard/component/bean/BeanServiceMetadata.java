/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard.component.bean;

import org.switchyard.Exchange;
import org.switchyard.Scope;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean Service meta data.
 * <p/>
 * Provides access to Bean Service operation invocation information.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanServiceMetadata {

    // TODO: needs to live somewhere else
    /**
     * Operation name key.
     */
    private static final String OPERATION_NAME = "OPERATION_NAME";

    /**
     * List of service methods/operations.
     */
    private List<Method> serviceMethods = new ArrayList<Method>();

    /**
     * Public constructor.
     *
     * @param serviceClass The service bean class.
     */
    public BeanServiceMetadata(Class<? extends Object> serviceClass) {
        Method[] serviceMethods = serviceClass.getMethods();
        for (Method serviceMethod : serviceMethods) {
            if (serviceMethod.getDeclaringClass() != Object.class) {
                this.serviceMethods.add(serviceMethod);
            }
        }
    }

    // TODO: needs to live somewhere else

    /**
     * Set the target service operation name of the supplied {@link Exchange} {@link org.switchyard.Context}.
     *
     * @param exchange The exchange instance.
     * @param name     The target service operation name.
     */
    public static void setOperationName(Exchange exchange, String name) {
        exchange.getContext(Scope.EXCHANGE).setProperty(OPERATION_NAME, name);
    }

    // TODO: needs to live somewhere else

    /**
     * Get the target service operation name from the supplied {@link Exchange} {@link org.switchyard.Context}.
     *
     * @param exchange The exchange instance.
     * @return The operation name as specified on the {@link Exchange} {@link org.switchyard.Context}.
     */
    public static String getOperationName(Exchange exchange) {
        return (String) exchange.getContext(Scope.EXCHANGE).getProperty(OPERATION_NAME);
    }

    /**
     * Get the Bean Service operation {@link Invocation} for the specified
     * {@link Exchange}.
     * <p/>
     * Uses the {@link #getOperationName(org.switchyard.Exchange)} method to extract
     * the service operation information from the {@link Exchange}.
     *
     * @param exchange The Exchange instance.
     * @return The operation {@link Invocation} instance.
     */
    public Invocation getInvocation(Exchange exchange) {

        String operationName = getOperationName(exchange);

        if (operationName != null) {
            List<Method> candidateMethods = getCandidateMethods(operationName);

            // Operation name must resolve to exactly one bean method...
            if (candidateMethods.size() != 1) {
                // TODO: sendFault ??? ...
                return null;
            }

            Method operationMethod = candidateMethods.get(0);
            return new Invocation(operationMethod, exchange.getMessage().getContent());
        } else {
            System.out.println("Operation name not specified on exchange.");
            // TODO: Operation name not specified... sendFault  ...
        }

        return null;
    }

    /**
     * Get the list of candidate service {@link Method methods/operations} for the specified
     * operation name.
     *
     * @param operationName The operation name.
     * @return The list of possible matching operation methods.
     */
    public List<Method> getCandidateMethods(String operationName) {
        List<Method> candidateMethods = new ArrayList<Method>();

        for (Method serviceMethod : serviceMethods) {
            if (serviceMethod.getName().equals(operationName)) {
                candidateMethods.add(serviceMethod);
            }
        }

        return candidateMethods;
    }

}
