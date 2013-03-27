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

    private static ThreadLocal<Context> _context = new ThreadLocal<Context>();

    @Override
    public Property getProperty(String name) {
        return getContext().getProperty(name);
    }

    @Override
    public Property getProperty(String name, Scope scope) {
        return getContext().getProperty(name, scope);
    }

    @Override
    public <T> T getPropertyValue(String name) {
        return getContext().getPropertyValue(name);
    }

    @Override
    public Set<Property> getProperties() {
        return getContext().getProperties();
    }

    @Override
    public Set<Property> getProperties(Scope scope) {
        return getContext().getProperties(scope);
    }

    @Override
    public void removeProperty(Property property) {
        getContext().removeProperty(property);
    }

    @Override
    public void removeProperties() {
        getContext().removeProperties();
    }

    @Override
    public void removeProperties(Scope scope) {
        getContext().removeProperties(scope);
    }

    @Override
    public Property setProperty(String name, Object val) {
        return getContext().setProperty(name, val);
    }

    @Override
    public Property setProperty(String name, Object val, Scope scope) {
        return getContext().setProperty(name, val, scope);
    }

    @Override
    public Context setProperties(Set<Property> properties) {
        return getContext().setProperties(properties);
    }

    @Override
    public Context copy() {
        return getContext().copy();
    }

    @Override
    public Set<Property> getProperties(String label) {
        return getContext().getProperties(label);
    }

    @Override
    public void removeProperties(String label) {
        getContext().removeProperties(label);
    }

    /**
     * Set the {@link Context} for the current thread.
     * @param context The context.
     */
    public static void setContext(Context context) {
        if (context != null) {
            _context.set(context);
        } else {
            _context.remove();
        }
    }

    /**
     * Get the {@link Context} for the current thread.
     * @return The context.
     */
    private static Context getContext() {
        Context context = _context.get();

        if (context == null) {
            throw new UnsupportedOperationException("Illegal call to get the SwitchYard Exchange Context.  Must be called within the scope of an Exchange Handler Chain.");
        }

        return context;
    }

}
