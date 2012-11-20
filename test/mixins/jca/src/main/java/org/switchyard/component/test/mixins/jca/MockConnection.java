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
        return new MockInteraction(_listener);
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
