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
package org.switchyard.internal.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.switchyard.internal.io.graph.GraphBuilder;
import org.switchyard.internal.io.graph.GraphWrapper;
import org.switchyard.io.BaseSerializer;
import org.switchyard.io.Serializer;

/**
 * de/serializes objects using a wrapped Serializer.
 * The object is broken down into a {@link org.switchyard.internal.io.graph.Graph Graph} on serialization, and re-constituted during deserialization.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class GraphSerializer extends BaseSerializer {

    private Serializer _ser;

    /**
     * Construction must include the defined Serializer for actual graph de/serialization.
     * @param ser the wrapped Serializer
     */
    public GraphSerializer(Serializer ser) {
        _ser = ser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> int serialize(T obj, Class<T> type, OutputStream out, int bufferSize) throws IOException {
        GraphWrapper<T> graph = new GraphWrapper<T>(GraphBuilder.build(obj));
        return _ser.serialize(graph, GraphWrapper.class, out, bufferSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T deserialize(InputStream in, Class<T> type, int bufferSize) throws IOException {
        @SuppressWarnings("unchecked")
        GraphWrapper<T> graph = _ser.deserialize(in, GraphWrapper.class, bufferSize);
        return graph.decompose();
    }

}
