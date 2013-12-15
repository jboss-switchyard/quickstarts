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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

import org.apache.log4j.Logger;

/**
 * MockManagedConnection.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
class MockManagedConnection implements ManagedConnection {

    private Logger _logger = Logger.getLogger(MockManagedConnection.class);
    private List<ConnectionEventListener> _listener = new ArrayList<ConnectionEventListener>();
    
    @Override
    public void addConnectionEventListener(ConnectionEventListener arg0) {
        _logger.debug("call addConnectionEventListener(" + arg0 + ")");
        _listener.add(arg0);
    }

    @Override
    public void associateConnection(Object arg0) throws ResourceException {
        _logger.debug("call associateConnection(" + arg0 + ")");
    }

    @Override
    public void cleanup() throws ResourceException {
        _logger.debug("call cleanup");
    }

    @Override
    public void destroy() throws ResourceException {
        _logger.debug("call destroy");
    }

    @Override
    public Object getConnection(Subject arg0, ConnectionRequestInfo arg1)
            throws ResourceException {
        _logger.debug("call getConnection(" + arg0 + ", " + arg1 + ")");
        return new MockConnection(new ArrayList<InteractionListener>());
    }

    @Override
    public LocalTransaction getLocalTransaction() throws ResourceException {
        _logger.debug("call getTransaction");
        return new LocalTransaction() {
            @Override
            public void begin() throws ResourceException {
            }

            @Override
            public void commit() throws ResourceException {
            }

            @Override
            public void rollback() throws ResourceException {
            }
        };
    }

    @Override
    public PrintWriter getLogWriter() throws ResourceException {
        _logger.debug("call getLogWriter");
        return null;
    }

    @Override
    public ManagedConnectionMetaData getMetaData() throws ResourceException {
        _logger.debug("call getMetaData");
        return new ManagedConnectionMetaData() {
            @Override
            public String getEISProductName() throws ResourceException {
                return null;
            }
            @Override
            public String getEISProductVersion() throws ResourceException {
                return null;
            }
            @Override
            public int getMaxConnections() throws ResourceException {
                return 0;
            }
            @Override
            public String getUserName() throws ResourceException {
                return null;
            }
        };
    }

    @Override
    public XAResource getXAResource() throws ResourceException {
        _logger.debug("call getXAResource");
        return new MockXAResource();
    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener arg0) {
        _logger.debug("call removeConnectionEventListener(" + arg0 + ")");
        _listener.remove(arg0);
    }

    @Override
    public void setLogWriter(PrintWriter arg0) throws ResourceException {
        _logger.debug("call setLogWriter(" + arg0 + ")");
    }
}
