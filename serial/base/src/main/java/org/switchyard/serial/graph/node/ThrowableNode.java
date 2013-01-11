/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.serial.graph.node;

import java.lang.reflect.Constructor;

import org.switchyard.HandlerException;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.common.type.reflect.FieldAccess;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.serial.graph.Graph;

/**
 * A node representing a Throwable.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@SuppressWarnings("serial")
public final class ThrowableNode implements Node {

    private static final Class<?>[][] PARAMETER_TYPES = new Class<?>[][]{
        new Class<?>[]{String.class},
        new Class<?>[0]
    };

    private Integer _clazz;
    private String _message;
    private Integer _cause;
    private Integer _stackTrace;
    private Boolean _wrapper;

    /**
     * Default constructor.
     */
    public ThrowableNode() {}

    /**
     * Gets the class.
     * @return the class.
     */
    public Integer getClazz() {
        return _clazz;
    }

    /**
     * Sets the class.
     * @param clazz the class
     */
    public void setClazz(Integer clazz) {
        _clazz = clazz;
    }

    /**
     * Gets the message.
     * @return the message
     */
    public String getMessage() {
        return _message;
    }

    /**
     * Sets the message.
     * @param message the message
     */
    public void setMessage(String message) {
        _message = message;
    }

    /**
     * Gets the cause.
     * @return the cause
     */
    public Integer getCause() {
        return _cause;
    }

    /**
     * Sets the cause.
     * @param cause the cause
     */
    public void setCause(Integer cause) {
        _cause = cause;
    }

    /**
     * Gets the stack trace.
     * @return the stack trace
     */
    public Integer getStackTrace() {
        return _stackTrace;
    }

    /**
     * Sets the stack trace.
     * @param stackTrace the stack trace
     */
    public void setStackTrace(Integer stackTrace) {
        _stackTrace = stackTrace;
    }

    /**
     * Gets the wrapper.
     * @return the wrapper
     */
    public Boolean getWrapper() {
        return _wrapper;
    }

    /**
     * Sets the wrapper.
     * @param wrapper the wrapper
     */
    public void setWrapper(Boolean wrapper) {
        _wrapper = wrapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void compose(Object obj, Graph graph) {
        Throwable throwable = (Throwable)obj;
        setClazz(NodeBuilder.build(throwable.getClass(), graph));
        setMessage(throwable.getMessage());
        setCause(NodeBuilder.build(throwable.getCause(), graph));
        setStackTrace(NodeBuilder.build(throwable.getStackTrace(), graph));
        if (throwable instanceof HandlerException) {
            setWrapper(((HandlerException)throwable).isWrapper());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object decompose(Graph graph) {
        Class<? extends Throwable> clazz = (Class<? extends Throwable>)graph.decomposeReference(getClazz());
        Throwable throwable = newThrowable(clazz, getMessage());
        Throwable cause = (Throwable)graph.decomposeReference(getCause());
        if (cause != null) {
            throwable.initCause(cause);
        }
        Object[] array = (Object[])graph.decomposeReference(getStackTrace());
        if (array != null) {
            StackTraceElement[] stackTrace = new StackTraceElement[array.length];
            for (int i=0; i < array.length; i++) {
                stackTrace[i] = (StackTraceElement)array[i];
            }
            throwable.setStackTrace(stackTrace);
        }
        if (throwable instanceof HandlerException && getWrapper() != null) {
            new FieldAccess<Boolean>(HandlerException.class, "_wrapper").write(throwable, getWrapper());
        }
        return throwable;
    }

    private Throwable newThrowable(Class<? extends Throwable> throwableClass, String message) {
        Throwable throwable = null;
        Constructor<? extends Throwable> constructor = getConstructor(throwableClass);
        Class<?>[] parameterTypes = constructor != null ? constructor.getParameterTypes() : new Class<?>[0];
        try {
            if (parameterTypes.length == 0) {
                throwable = Construction.construct(throwableClass);
            } else if (parameterTypes.length == 1) {
                throwable = Construction.construct(throwableClass, parameterTypes, new Object[]{message});
            }
        } catch (Throwable t) {
            throw new SwitchYardException("Could not instantiate Throwable class: " + throwableClass.getName());
        }
        return throwable;
    }

    private Constructor<? extends Throwable> getConstructor(Class<? extends Throwable> throwableClass) {
        Constructor<? extends Throwable> constructor = null;
        for (Class<?>[] parameterTypes : PARAMETER_TYPES) {
            try {
                constructor = throwableClass.getConstructor(parameterTypes);
                if (constructor != null) {
                    break;
                }
            } catch (Throwable t) {
                // keep checkstyle happy ("at least one statement")
                t.getMessage();
            }
        }
        return constructor;
    }

}
