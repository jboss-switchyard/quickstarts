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

    /**
     * Service bean component runtime class.
     */
    private Class<? extends Object> _serviceClass;
    /**
     * List of service methods/operations.
     */
    private List<Method> _serviceMethods = new ArrayList<Method>();

    /**
     * Public constructor.
     *
     * @param serviceClass The service bean class.
     */
    public BeanServiceMetadata(Class<? extends Object> serviceClass) {
        Method[] serviceMethods = serviceClass.getMethods();
        for (Method serviceMethod : serviceMethods) {
            if (serviceMethod.getDeclaringClass() != Object.class) {
                this._serviceMethods.add(serviceMethod);
            }
        }
        this._serviceClass = serviceClass;
    }

    /**
     * Get the Bean Service operation {@link Invocation} for the specified
     * {@link Exchange}.
     * <p/>
     * Uses the {@link org.switchyard.metadata.ServiceOperation.Name#get(org.switchyard.Exchange)} method to extract
     * the service operation information from the {@link Exchange}.
     *
     * @param exchange The Exchange instance.
     * @return The operation {@link Invocation} instance.
     * @throws BeanComponentException Error invoking Bean component operation.
     */
    public Invocation getInvocation(Exchange exchange) throws BeanComponentException {

        String operationName = exchange.getContract().getServiceOperation().getName();

        if (operationName != null) {
            List<Method> candidateMethods = getCandidateMethods(operationName);

            // Operation name must resolve to exactly one bean method...
            if (candidateMethods.size() != 1) {
                throw new BeanComponentException("Operation name '" + operationName + "' must resolve to exactly one bean method on bean type '" + _serviceClass.getName() + "'.");
            }

            Method operationMethod = candidateMethods.get(0);
            return new Invocation(operationMethod, exchange);
        } else {
            throw new BeanComponentException("Operation name not specified on exchange.");
        }
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

        for (Method serviceMethod : _serviceMethods) {
            if (serviceMethod.getName().equals(operationName)) {
                candidateMethods.add(serviceMethod);
            }
        }

        return candidateMethods;
    }

}
