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
        return null;
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
