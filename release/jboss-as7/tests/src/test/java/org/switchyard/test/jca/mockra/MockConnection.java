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

import java.util.List;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionMetaData;
import javax.resource.cci.Interaction;
import javax.resource.cci.LocalTransaction;
import javax.resource.cci.ResultSetInfo;

import org.apache.log4j.Logger;

/**
 * MockConnection.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class MockConnection implements Connection {
    private Logger _logger = Logger.getLogger(MockConnection.class);
    private List<InteractionListener> _listener;
    
    /**
     * Constructor.
     * @param listener InteractionListener.
     */
    public MockConnection(List<InteractionListener> listener) {
        _listener = listener;
    }
    
    @Override
    public Interaction createInteraction() throws ResourceException {
        _logger.debug("call createInteraction()");
        return new MockInteraction(_listener, this);
    }

    @Override
    public LocalTransaction getLocalTransaction() throws ResourceException {
        _logger.debug("call getLocalTransaction()");
        return null;
    }

    @Override
    public ConnectionMetaData getMetaData() throws ResourceException {
        _logger.debug("call getMetaData()");
        return null;
    }

    @Override
    public ResultSetInfo getResultSetInfo() throws ResourceException {
        _logger.debug("call getResultSetInfo()");
        return null;
    }

    @Override
    public void close() throws ResourceException {
        _logger.debug("call close()");
    }
}
