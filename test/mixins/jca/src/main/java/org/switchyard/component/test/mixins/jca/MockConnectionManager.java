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

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnectionFactory;

import org.apache.log4j.Logger;

/**
 * MockConnectionManager.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class MockConnectionManager implements ConnectionManager {

    private Logger _logger = Logger.getLogger(MockConnectionManager.class);
    private static final long serialVersionUID = 1L;
    
    @Override
    public Object allocateConnection(ManagedConnectionFactory arg0,
            ConnectionRequestInfo arg1) throws ResourceException {
        _logger.debug("call allocateConnection(" + arg0 + ", " + arg1 + ")");
        return arg0.createManagedConnection(null, arg1).getConnection(null, arg1);
    }
}
