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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.switchyard.serial.graph.Graph;

/**
 * A node representing a DataSource.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@SuppressWarnings("serial")
public final class DataSourceNode implements Node {

    private String _name;
    private String _contentType;
    private Integer _inputStreamId;

    /**
     * Default constructor.
     */
    public DataSourceNode() {}

    /**
     * Gets the name.
     * @return the name
     */
    public String getName() {
        return _name;
    }

    /**
     * Sets the name.
     * @param name the name
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * Gets the content type.
     * @return the content type
     */
    public String getContentType() {
        return _contentType;
    }

    /**
     * Sets the content type.
     * @param contentType the content type
     */
    public void setContentType(String contentType) {
        _contentType = contentType;
    }

    /**
     * Gets the input stream node id.
     * @return the input stream node id
     */
    public Integer getInputStreamId() {
        return _inputStreamId;
    }

    /**
     * Sets the input stream node id.
     * @param inputStreamId the input stream node id
     */
    public void setInputStreamId(Integer inputStreamId) {
        _inputStreamId = inputStreamId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void compose(Object obj, Graph graph) {
        DataSource ds = (DataSource)obj;
        setName(ds.getName());
        setContentType(ds.getContentType());
        try {
            setInputStreamId(NodeBuilder.build(ds.getInputStream(), graph));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object decompose(Graph graph) {
        InputStream is = (InputStream)graph.decomposeReference(getInputStreamId());
        return new NodeDataSource(getName(), getContentType(), is);
    }

    private static final class NodeDataSource implements DataSource {

        private String _name;
        private String _contentType;
        private InputStream _inputStream;

        public NodeDataSource(String name, String contentType, InputStream inputStream) {
            _name = name;
            _contentType = contentType;
            _inputStream = inputStream;
        }

        public String getName() {
            return _name;
        }

        public String getContentType() {
            return _contentType;
        }

        public InputStream getInputStream() throws IOException {
            return _inputStream;
        }

        public OutputStream getOutputStream() throws IOException {
            throw new UnsupportedOperationException();
        }

    }

}
