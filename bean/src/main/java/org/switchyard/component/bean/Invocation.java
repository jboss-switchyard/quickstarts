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

package org.switchyard.component.bean;

import org.switchyard.Exchange;

import java.lang.reflect.Method;

/**
 * Bean component invocation details.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Invocation {

    /**
     * The method/operation being invoked.
     */
    private Method _method;
    /**
     * The exchange instance.
     */
    private Exchange _exchange;

    /**
     * The invocation arguments.
     */
    private Object[] _args;

    /**
     * Constructor.
     *
     * @param method The method/operation being invoked.
     * @param exchange The exchange instance.
     * @throws BeanComponentException Unsupported method structure, or type mismatch.
     */
    Invocation(Method method, Exchange exchange) throws BeanComponentException {
        this._method = method;
        this._exchange = exchange;
        this._args = castArg(method, exchange.getMessage().getContent());
        assertOK();
    }

    /**
     * Assert that the exchange payload type(s) and the bean method
     * argument type(s) match.
     */
    private void assertOK() throws BeanComponentException {
        assertMethodStructureSupported();
        assertTypesMatch();
    }

    /**
     * Get the invocation arguments.
     *
     * @return The invocation arguments.
     */
    public Object[] getArgs() {
        return _args;
    }

    /**
     * Get the method/operation being invoked.
     *
     * @return The method/operation being invoked.
     */
    public Method getMethod() {
        return _method;
    }

    private static Object[] castArg(Method method, Object content) {
        if (method.getParameterTypes().length == 1 && content != null) {
            if (content.getClass().isArray()) {
                return (Object[].class).cast(content);
            } else {
                return new Object[]{content};
            }
        }
        return null;
    }

    private void assertMethodStructureSupported() throws BeanComponentException {
        Class<?>[] parameterTypes = _method.getParameterTypes();

        // TODO: Only supports 0 or 1 arg operations for now...
        if (parameterTypes.length > 1) {
            throw new BeanComponentException("Bean service operation '" + operationName() + "' has more than 1 argument.  Bean component currently only supports single argument operations.");
        }
    }

    private void assertTypesMatch() throws BeanComponentException {
        if (_args == null) {
            if (_method.getParameterTypes().length != 0) {
                throw new BeanComponentException("Bean service operation '" + operationName() + "' requires a single argument.  Exchange payload specifies no payload.");
            }
        } else {
            if (_args.length > 1) {
                throw new BeanComponentException("Bean service operation '" + operationName() + "' only supports a single argument.  Exchange payload specifies " + _args.length + " args.");
            }

            if (_args[0] != null) {
                Class<?> argType = _method.getParameterTypes()[0];

                if (!argType.isInstance(_args[0])) {
                    throw new BeanComponentException("Bean service operation '" + operationName() + "' requires a payload type of '" + argType.getName() + "'.  Actual payload type is '" + _args[0].getClass().getName() + "'.  You must define and register a Transformer.");
                }
            }
        }
    }

    private String operationName() {
        return _exchange.getService().getName() + "#" + _method.getName();
    }
}
