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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.activation.DataSource;

/**
 * A Graph representing an activation DataSource, internalized as it's name, contentType and an InputStreamGraph.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
@SuppressWarnings("serial")
public class DataSourceGraph implements Graph<DataSource> {

    private String _name;
    private String _contentType;
    private InputStreamGraph _inputStreamGraph;

    /**
     * Gets the name of the DataSource.
     * @return the name of the DataSource
     */
    public String getName() {
        return _name;
    }

    /**
     * Sets the c of the DataSource.
     * @param name the name of the DataSource
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * Gets the contentType of the DataSource.
     * @return the contentType of the DataSource
     */
    public String getContentType() {
        return _contentType;
    }

    /**
     * Sets the contentType of the DataSource.
     * @param contentType the contentType of the DataSource
     */
    public void setContentType(String contentType) {
        _contentType = contentType;
    }

    /**
     * Sets the InputStreamGraph of the DataSource.
     * @return the InputStreamGraph of the DataSource
     */
    public InputStreamGraph getInputStreamGraph() {
        return _inputStreamGraph;
    }

    /**
     * Gets the InputStreamGraph of the DataSource.
     * @param inputStreamGraph the InputStreamGraph of the DataSource
     */
    public void setInputStreamGraph(InputStreamGraph inputStreamGraph) {
        _inputStreamGraph = inputStreamGraph;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void compose(DataSource object, Map<Integer,Object> visited) throws IOException {
        setName(object.getName());
        setContentType(object.getContentType());
        InputStreamGraph isg = new InputStreamGraph();
        isg.compose(object.getInputStream(), visited);
        setInputStreamGraph(isg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSource decompose(Map<Integer,Object> visited) throws IOException {
        return new GraphDataSource(this);
    }

    @Override
    public String toString() {
        return "DataSourceGraph(name=" + getName() + ", contentType=" + getContentType() + ", inputStreamGraph=" + getInputStreamGraph() + ")";
    }

    /**
     * A DataSource implementation which is used during Graph decomposition.
     */
    private static final class GraphDataSource implements DataSource {

        private DataSourceGraph _graph;

        /**
         * Constructs a new GraphDataSource.
         * @param graph the DataSourceGraph to wrap
         */
        public GraphDataSource(DataSourceGraph graph) {
            _graph = graph;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getName() {
            return _graph.getName();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getContentType() {
            return _graph.getContentType();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public InputStream getInputStream() throws IOException {
            return _graph.getInputStreamGraph().decompose(null);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public OutputStream getOutputStream() throws IOException {
            throw new UnsupportedOperationException();
        }

    }

}
