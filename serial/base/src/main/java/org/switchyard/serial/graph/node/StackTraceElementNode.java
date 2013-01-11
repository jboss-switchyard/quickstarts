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
