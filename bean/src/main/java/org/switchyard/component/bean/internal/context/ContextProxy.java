/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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

package org.switchyard.component.bean.internal.context;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.Scope;

/**
 * SwitchYard Context proxy.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@ApplicationScoped
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
            throw new IllegalStateException("Illegal call to get the SwitchYard Context; must be called within the execution of an ExchangeHandler chain.");
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
