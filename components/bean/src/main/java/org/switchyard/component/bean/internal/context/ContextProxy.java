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

package org.switchyard.component.bean.internal.context;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.component.bean.BeanMessages;

/**
 * SwitchYard Context proxy.
 * <p/>
 * Injection is managed through ContextBean.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@ApplicationScoped
@Alternative
public class ContextProxy implements Context {

    private static final ThreadLocal<Context> CONTEXT = new ThreadLocal<Context>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Property getProperty(String name) {
        return getContext().getProperty(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property getProperty(String name, Scope scope) {
        return getContext().getProperty(name, scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getPropertyValue(String name) {
        return getContext().getPropertyValue(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Property> getProperties() {
        return getContext().getProperties();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Property> getProperties(Scope scope) {
        return getContext().getProperties(scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeProperty(Property property) {
        getContext().removeProperty(property);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeProperties() {
        getContext().removeProperties();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeProperties(Scope scope) {
        getContext().removeProperties(scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property setProperty(String name, Object val) {
        return getContext().setProperty(name, val);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property setProperty(String name, Object val, Scope scope) {
        return getContext().setProperty(name, val, scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Context setProperties(Set<Property> properties) {
        return getContext().setProperties(properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mergeInto(Context context) {
        getContext().mergeInto(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Property> getProperties(String label) {
        return getContext().getProperties(label);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeProperties(String label) {
        getContext().removeProperties(label);
    }

    /**
     * Gets the {@link Context} for the current thread.
     * @return the context
     */
    private static Context getContext() {
        Context context = CONTEXT.get();
        if (context == null) {
            throw BeanMessages.MESSAGES.illegalCallToGetTheSwitchYardContextMustBeCalledWithinTheExecutionOfAnExchangeHandlerChain();
        }
        return context;
    }

    /**
     * Sets the {@link Context} for the current thread.
     * @param context the context
     */
    public static void setContext(Context context) {
        if (context != null) {
            CONTEXT.set(context);
        } else {
            CONTEXT.remove();
        }
    }

}
