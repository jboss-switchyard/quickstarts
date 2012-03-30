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

import java.io.Serializable;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.resource.Referenceable;
import javax.resource.spi.ConnectionManager;

import org.apache.log4j.Logger;

/**
 * MockConnectionFactory.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class MockConnectionFactory implements Serializable, Referenceable {

    private static final long serialVersionUID = 1L;
    private Logger _logger = Logger.getLogger(MockConnectionFactory.class);
    private ConnectionManager _cm;
    private Reference _ref;

    /**
     * Constructor.
     * 
     * @param cm {@link ConnectionManager}
     */
    public MockConnectionFactory(ConnectionManager cm) {
        _cm = cm;
        _ref = null;
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
}
