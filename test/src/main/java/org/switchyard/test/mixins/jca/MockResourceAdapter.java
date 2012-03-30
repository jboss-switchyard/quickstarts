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

import java.lang.reflect.Method;

import javax.resource.ResourceException;
import javax.resource.cci.MessageListener;
import javax.resource.cci.Record;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;

import org.apache.log4j.Logger;

/**
 * MockResourceAdapter.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class MockResourceAdapter implements ResourceAdapter {

    private Logger _logger = Logger.getLogger(MockResourceAdapter.class);
    private ActivationSpec _spec = null;
    private MessageEndpointFactory _factory = null;
    private MessageEndpoint _endpoint = null;
    
    @Override
    public void endpointActivation(MessageEndpointFactory arg0,
            ActivationSpec arg1) throws ResourceException {
        _spec = arg1;
        _factory = arg0;
        _logger.debug("call endpointActivation(" + arg0 + ", " + arg1 + ")");
    }
    
    @Override
    public void endpointDeactivation(MessageEndpointFactory arg0,
            ActivationSpec arg1) {
        _logger.debug("call endpointDeactivation(" + arg0 + ", " + arg1 + ")");
    }
    
    @Override
    public XAResource[] getXAResources(ActivationSpec[] arg0)
            throws ResourceException {
        _logger.debug("call getXAResources(" + arg0 + ")");
        return null;
    }
    
    @Override
    public void start(BootstrapContext arg0)
            throws ResourceAdapterInternalException {
        _logger.debug("call start(" + arg0 + ")");
    }
    
    @Override
    public void stop() {
        _logger.debug("call stop");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MockResourceAdapter) {
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    /**
     * fire createEndpoint().
     */
    public void fireCreateEndpoint() {
        _logger.debug("call fireCreateEndpoint");
        try {
            _endpoint = _factory.createEndpoint(new MockXAResource(), 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * fire beforeDelivery().
     */
    public void fireBeforeDelivery() {
        _logger.debug("call fireBeforeDelivery");
       try {
           _endpoint.beforeDelivery(null);
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
    }
    
    /**
     * fire message delivery via {@link MessageListener#onMessage()}.
     * 
     * @param record {@link Record} instance to deliver
     * @return returned value
     */
    public Record fireDelivery(Record record) {
        _logger.debug("call onMessage: record=" + record);
        
        try {
            Method m = MessageListener.class.getMethod("onMessage", new Class[]{Record.class});
            return Record.class.cast(m.invoke(_endpoint, new Object[]{record}));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * fire afterDelivery().
     */
    public void fireAfterDelivery() {
        _logger.debug("call fireAfterDelivery");
        try {
            _endpoint.afterDelivery();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * fire release().
     */
    public void fireRelease() {
        _logger.debug("call fireRelease");
        _endpoint.release();
    }
}
