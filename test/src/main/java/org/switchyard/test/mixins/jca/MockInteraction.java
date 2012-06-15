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

import java.util.List;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.Interaction;
import javax.resource.cci.InteractionSpec;
import javax.resource.cci.Record;
import javax.resource.cci.ResourceWarning;

import org.apache.log4j.Logger;

/**
 * MockInteraction.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class MockInteraction implements Interaction {
    private Logger _logger = Logger.getLogger(MockInteraction.class);
    private List<InteractionListener> _listener;
    /**
     * Constructor.
     * 
     * @param listener InteractionListener
     */
    public MockInteraction(List<InteractionListener> listener) {
        _listener = listener;
    }
    
    @Override
    public void close() throws ResourceException {
        _logger.debug("call close()");
    }

    @Override
    public Connection getConnection() {
        _logger.debug("call getConnection()");
        return null;
    }

    @Override
    public boolean execute(InteractionSpec ispec, Record input, Record output)
            throws ResourceException {
        _logger.debug("call execute(" + ispec + ", " + input + ", " + output + ")");
        boolean result = true;
        for (InteractionListener l : _listener) {
            result &= l.onExecute(ispec, input, output);
        }
        return result;
    }

    @Override
    public Record execute(InteractionSpec ispec, Record input)
            throws ResourceException {
        _logger.debug("call execute(" + ispec + ", " + input + ")");
        Record output = null;
        for (InteractionListener l : _listener) {
            output = l.onExecute(ispec, input);
            input = output;
        }
        return output;
    }

    @Override
    public ResourceWarning getWarnings() throws ResourceException {
        _logger.debug("call getWarnings()");
        return null;
    }

    @Override
    public void clearWarnings() throws ResourceException {
        _logger.debug("call clearWarnings()");
    }

}
