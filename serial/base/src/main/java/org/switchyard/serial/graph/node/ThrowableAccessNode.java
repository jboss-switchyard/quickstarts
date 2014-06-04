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
package org.switchyard.serial.graph.node;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.switchyard.HandlerException;
import org.switchyard.common.type.reflect.FieldAccess;
import org.switchyard.serial.graph.Graph;

/**
 * A node representing a Throwable.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
@SuppressWarnings("serial")
public final class ThrowableAccessNode extends AccessNode {

    static final Set<String> THROWABLE_IGNORED_ACCESS_NAMES;
    static {
        Set<String> throwableIgnoredAccessNames = new HashSet<String>();
        throwableIgnoredAccessNames.addAll(AccessNode.IGNORED_ACCESS_NAMES);
        throwableIgnoredAccessNames.add("message");
        throwableIgnoredAccessNames.add("cause");
        throwableIgnoredAccessNames.add("stackTrace");
        THROWABLE_IGNORED_ACCESS_NAMES = Collections.unmodifiableSet(throwableIgnoredAccessNames);
    }

    private Integer _clazz;
    private Map<String, Integer> _ids;
    private String _message;
    private Integer _cause;
    private Integer _stackTrace;
    private Boolean _wrapper;

    /**
     * Default constructor.
     */
    public ThrowableAccessNode() {}

    /**
     * {@inheritDoc}
     */
    @Override
    Set<String> getIgnoredAccessNames() {
        return THROWABLE_IGNORED_ACCESS_NAMES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getClazz() {
        return _clazz;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setClazz(Integer clazz) {
        _clazz = clazz;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Integer> getIds() {
        return _ids;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIds(Map<String, Integer> ids) {
        _ids = ids;
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
        super.compose(obj, graph);
        Throwable throwable = (Throwable)obj;
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
    public Object decompose(Graph graph) {
        Throwable throwable = (Throwable)super.decompose(graph);
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

}
