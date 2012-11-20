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
    
    /**
     * Constructor.
     */
    public MockActivationSpec() {
        _logger.debug("instantiate MockResourceAdapter");
    }
    
    @Override
    public ResourceAdapter getResourceAdapter() {
        _logger.debug("call getResourceAdapter");
        return null;
    }
    @Override
    public void setResourceAdapter(ResourceAdapter arg0)
            throws ResourceException {
        _logger.debug("call setResourceAdapter(" + arg0 + ")");
    }
    @Override
    public void validate() throws InvalidPropertyException {
        _logger.debug("call validate");
    }
}
