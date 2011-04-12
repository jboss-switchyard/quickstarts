/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.internal.io.graph;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * Represents a node in an object graph.
 *
 * @param <T> the type of object at the root of this Graph node
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface Graph<T> extends Serializable {

    /**
     * Populates this Graph with the given object.
     * @param object the object to graph
     * @param visited to handle cyclic references so as not to get a StackOverflowError
     * @throws IOException if a problem occurs during graphing
     */
    public void compose(T object, Map<Integer,Object> visited) throws IOException;

    /**
     * Extracts the object from this Graph.
     * @param visited to handle cyclic references so as not to get a StackOverflowError
     * @return the extracted object
     * @throws IOException if a problem occurs during "de-graphing"
     */
    public T decompose(Map<Integer,Object> visited) throws IOException;

}
