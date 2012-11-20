/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.component.test.mixins.jca;

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

    private Logger _logger = Logger.getLogger(MockManagedConnectionFactory.class);
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
        return obj.equals(this);
    }
    
    @Override
    public int hashCode() {
        return this.hashCode();
    }
}
