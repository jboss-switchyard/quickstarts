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
import java.util.Set;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.security.auth.Subject;

import org.apache.log4j.Logger;

/**
 * MockManagedConnectionFactory.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class MockManagedConnectionFactory implements ManagedConnectionFactory {

    private transient Logger _logger = Logger.getLogger(MockManagedConnectionFactory.class);
    private static final long serialVersionUID = 1L;

    @Override
    public Object createConnectionFactory() throws ResourceException {
        _logger.debug("call createConnectionFactory");
        return new MockConnectionFactory(new MockConnectionManager());
    }

    @Override
    public Object createConnectionFactory(ConnectionManager arg0)
            throws ResourceException {
        _logger.debug("call createConnectionFactory(" + arg0 + ")");
        return new MockConnectionFactory(arg0);
    }

    @Override
    public ManagedConnection createManagedConnection(Subject arg0,
            ConnectionRequestInfo arg1) throws ResourceException {
        _logger.debug("call createManagedConnection(" + arg0 + ", " + arg1 + ")");
        return new MockManagedConnection();
    }

    @Override
    public PrintWriter getLogWriter() throws ResourceException {
        _logger.debug("call getLogWriter");
        return null;
    }

    @Override
    public ManagedConnection matchManagedConnections(Set arg0,
            Subject arg1, ConnectionRequestInfo arg2)
            throws ResourceException {
        _logger.debug("call matchManagedConnections(" + arg0 + ", " + arg1 + ", " + arg2 + ")");
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter arg0) throws ResourceException {
        _logger.debug("call setLogWriter(" + arg0 + ")");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            return obj.equals(this);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
