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
package org.switchyard.test.mixins.jca;

import java.io.PrintWriter;

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
    
    @Override
    public void addConnectionEventListener(ConnectionEventListener arg0) {
        _logger.debug("call addConnectionEventListener(" + arg0 + ")");
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
        return null;
    }

    @Override
    public LocalTransaction getLocalTransaction() throws ResourceException {
        _logger.debug("call getTransaction");
        return null;
    }

    @Override
    public PrintWriter getLogWriter() throws ResourceException {
        _logger.debug("call getLogWriter");
        return null;
    }

    @Override
    public ManagedConnectionMetaData getMetaData() throws ResourceException {
        _logger.debug("call getMetaData");
        return null;
    }

    @Override
    public XAResource getXAResource() throws ResourceException {
        _logger.debug("call getXAResource");
        return null;
    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener arg0) {
        _logger.debug("call removeConnectionEventListener(" + arg0 + ")");
    }

    @Override
    public void setLogWriter(PrintWriter arg0) throws ResourceException {
        _logger.debug("call setLogWriter(" + arg0 + ")");
    }
}
