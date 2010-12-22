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
    private Method method;
    /**
     * The invocation arguments.
     */
    private Object[] args;

    /**
     * Constructor.
     *
     * @param method The method/operation being invoked.
     * @param arg    The invocation arguments.
     */
    Invocation(Method method, Object arg) {
        this.method = method;
        this.args = castArg(arg);
    }

    /**
     * Get the method/operation being invoked.
     *
     * @return The method/operation being invoked.
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Get the invocation arguments.
     *
     * @return The invocation arguments.
     */
    public Object[] getArgs() {
        return args;
    }

    private static Object[] castArg(Object arg) {
        if (arg.getClass().isArray()) {
            return (Object[].class).cast(arg);
        } else {
            return new Object[]{arg};
        }
    }
}
