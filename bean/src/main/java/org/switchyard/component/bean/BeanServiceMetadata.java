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
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanServiceMetadata {

    // TODO: needs to live somewhere else
    private static final String OPERATION_NAME = "OPERATION_NAME";

    private List<Method> serviceMethods = new ArrayList<Method>();

    public BeanServiceMetadata(Class<? extends Object> serviceClass) {
        Method[] serviceMethods = serviceClass.getMethods();
        for(Method serviceMethod : serviceMethods) {
            if(serviceMethod.getDeclaringClass() != Object.class) {
                this.serviceMethods.add(serviceMethod);
            }
        }
    }

    // TODO: needs to live somewhere else
    public static void setOperationName(Exchange exchange, String name) {
        exchange.getContext(Scope.EXCHANGE).setProperty(OPERATION_NAME, name);
    }

    // TODO: needs to live somewhere else
    public static String getOperationName(Exchange exchange) {
        return (String) exchange.getContext(Scope.EXCHANGE).getProperty(OPERATION_NAME);
    }

    public Invocation getInvocation(Exchange exchange) {

        String operationName = getOperationName(exchange);

        if(operationName != null) {
            List<Method> candidateMethods = getCandidateMethods(operationName);

            // Operation name must resolve to exactly one bean method...
            if(candidateMethods.size() != 1) {
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

    public List<Method> getCandidateMethods(String name) {
        List<Method> candidateMethods = new ArrayList<Method>();

        for(Method serviceMethod : serviceMethods) {
            if(serviceMethod.getName().equals(name)) {
                candidateMethods.add(serviceMethod);
            }
        }

        return candidateMethods;
    }

    public static class Invocation {
        private Method method;
        private Object[] args;

        private Invocation(Method method, Object arg) {
            this.method = method;
            this.args = castArg(arg);
        }

        private static Object[] castArg(Object arg) {
            if(arg.getClass().isArray()) {
                return (Object[].class).cast(arg);
            } else {
                return new Object[] {arg};
            }
        }

        public Method getMethod() {
            return method;
        }

        public Object[] getArgs() {
            return args;
        }
    }

}
