/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.common.knowledge.transaction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * TransactionInvocationHandler.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class TransactionInvocationHandler implements InvocationHandler {

    private final Object _target;
    private final boolean _enabled;

    /**
     * Creates a new, enabled TransactionInvocationHandler.
     * @param target the target object
     */
    public TransactionInvocationHandler(Object target) {
        this(target, true);
    }

    /**
     * Creates a new TransactionInvocationHandler.
     * @param target the target object
     * @param enabled whether transactions are enabled or not
     */
    public TransactionInvocationHandler(Object target, boolean enabled) {
        _target = target;
        _enabled = enabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (_target == null) {
            return null;
        } else if (!_enabled || Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(_target, args);
        } else {
            Object ret;
            TransactionHelper utx = new TransactionHelper(_enabled);
            try {
                utx.begin();
                ret = method.invoke(_target, args);
                utx.commit();
            } catch (Throwable t) {
                utx.rollback();
                throw t;
            }
            return ret;
        }
    }

}
