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

package org.switchyard.component.bean;

import org.switchyard.Exchange;
import org.switchyard.Message;

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
        this._args = castArg(method, exchange.getMessage());
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

    private static Object[] castArg(Method method, Message message) {
        if (method.getParameterTypes().length == 1 && message != null) {
            return new Object[]{message.getContent(method.getParameterTypes()[0])};
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
        return _exchange.getProvider().getName() + "#" + _method.getName();
    }
}
