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
package org.switchyard.test.jca.mockra;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.resource.Referenceable;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.ConnectionSpec;
import javax.resource.cci.RecordFactory;
import javax.resource.cci.ResourceAdapterMetaData;
import javax.resource.spi.ConnectionManager;

import org.apache.log4j.Logger;

/**
 * MockConnectionFactory.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class MockConnectionFactory implements ConnectionFactory, Serializable, Referenceable {

    private static final long serialVersionUID = 1L;
    private Logger _logger = Logger.getLogger(MockConnectionFactory.class);
    private ConnectionManager _cm;
    private Reference _ref;
    private List<InteractionListener> _listener = new ArrayList<InteractionListener>();
    
    /**
     * Constructor.
     */
    public MockConnectionFactory() {
    }
    
    /**
     * Constructor.
     * 
     * @param cm {@link ConnectionManager}
     */
    public MockConnectionFactory(ConnectionManager cm) {
        _logger.debug("call MockConnectionFactory(" + cm + ")");
        _cm = (ConnectionManager) cm;
        _ref = null;
    }
    @Override
    public Connection getConnection() throws ResourceException {
        _logger.debug("call getConnection");
        return new MockConnection(_listener);
    }
    @Override
    public Connection getConnection(ConnectionSpec properties)
            throws ResourceException {
        _logger.debug("call getConnection(" + properties + ")");
        return new MockConnection(_listener);
    }
    @Override
    public RecordFactory getRecordFactory() throws ResourceException {
        _logger.debug("call getRecordFactory");
        return new MockRecordFactory();
    }
    @Override
    public ResourceAdapterMetaData getMetaData() throws ResourceException {
        _logger.debug("call getMetaData");
        return new ResourceAdapterMetaData() {
            @Override
            public String getAdapterVersion() {
                return null;
            }
            @Override
            public String getAdapterVendorName() {
                return null;
            }
            @Override
            public String getAdapterName() {
                return null;
            }
            @Override
            public String getAdapterShortDescription() {
                return null;
            }
            @Override
            public String getSpecVersion() {
                return null;
            }
            @Override
            public String[] getInteractionSpecsSupported() {
                return null;
            }
            @Override
            public boolean supportsExecuteWithInputAndOutputRecord() {
                return false;
            }
            @Override
            public boolean supportsExecuteWithInputRecordOnly() {
                return false;
            }
            @Override
            public boolean supportsLocalTransactionDemarcation() {
                return false;
            }
        };
    }
    @Override
    public Reference getReference() throws NamingException {
        _logger.debug("call getReference");
        return _ref;
    }
    @Override
    public void setReference(Reference ref) {
        _logger.debug("call setReference(" + ref + ")");
        _ref = ref;
    }

    /**
     * set InteractionListener.
     * @param listener InteractionListener
     */
    public void setInteractionListener(InteractionListener listener) {
        _listener.add(listener);
    }
}
