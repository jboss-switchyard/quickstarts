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
package org.switchyard.component.test.mixins.jca;

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
