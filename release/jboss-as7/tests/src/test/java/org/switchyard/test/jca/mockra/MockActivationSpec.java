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

import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.InvalidPropertyException;
import javax.resource.spi.ResourceAdapter;

import org.apache.log4j.Logger;

/**
 * MockActivationSpec.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class MockActivationSpec implements ActivationSpec {
    private Logger _logger = Logger.getLogger(MockActivationSpec.class);
    private ResourceAdapter _adapter;
    
    /**
     * Constructor.
     */
    public MockActivationSpec() {
        _logger.debug("instantiate MockResourceAdapter");
    }
    
    @Override
    public ResourceAdapter getResourceAdapter() {
        _logger.debug("call getResourceAdapter");
        return _adapter;
    }
    @Override
    public void setResourceAdapter(ResourceAdapter arg0)
            throws ResourceException {
        _logger.debug("call setResourceAdapter(" + arg0 + ")");
        _adapter = arg0;
    }
    @Override
    public void validate() throws InvalidPropertyException {
        _logger.debug("call validate");
    }
}
