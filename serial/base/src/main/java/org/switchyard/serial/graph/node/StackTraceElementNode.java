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
package org.switchyard.serial.graph.node;

import org.switchyard.serial.graph.Graph;

/**
 * A node representing a StackTraceElement.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@SuppressWarnings("serial")
public final class StackTraceElementNode implements Node {

    private String _className;
    private String _methodName;
    private String _fileName;
    private int _lineNumber;

    /**
     * Default constructor.
     */
    public StackTraceElementNode() {}

    /**
     * Gets the class name.
     * @return the class name
     */
    public String getClassName() {
        return _className;
    }

    /**
     * Sets the class name.
     * @param className the class name
     */
    public void setClassName(String className) {
        _className = className;
    }

    /**
     * Gets the method name.
     * @return the method name
     */
    public String getMethodName() {
        return _methodName;
    }

    /**
     * Sets the method name.
     * @param methodName the method name
     */
    public void setMethodName(String methodName) {
        _methodName = methodName;
    }

    /**
     * Gets the file name.
     * @return the file name
     */
    public String getFileName() {
        return _fileName;
    }

    /**
     * Sets the file name.
     * @param fileName the file name
     */
    public void setFileName(String fileName) {
        _fileName = fileName;
    }

    /**
     * Gets the line number.
     * @return the line number
     */
    public int getLineNumber() {
        return _lineNumber;
    }

    /**
     * Sets the line number.
     * @param lineNumber the line number
     */
    public void setLineNumber(int lineNumber) {
        _lineNumber = lineNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void compose(Object obj, Graph graph) {
        StackTraceElement ste = (StackTraceElement)obj;
        setClassName(ste.getClassName());
        setMethodName(ste.getMethodName());
        setFileName(ste.getFileName());
        setLineNumber(ste.getLineNumber());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object decompose(Graph graph) {
        return new StackTraceElement(getClassName(), getMethodName(), getFileName(), getLineNumber());
    }

}
