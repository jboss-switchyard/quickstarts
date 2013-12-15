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

import java.lang.reflect.Method;

import javax.naming.InitialContext;
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

    public MockResourceAdapter() {
        try {
            InitialContext ic = new InitialContext();
            ic.bind(MockResourceAdapterUtil.JNDI_ADAPTER, this);
            _logger.info("Bound to " + MockResourceAdapterUtil.JNDI_ADAPTER);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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
        StringBuffer buffer = new StringBuffer();
        for (ActivationSpec as : arg0) {
            buffer.append("[" + as.toString() + "] ");
        }
        buffer.trimToSize();
        _logger.debug("call getXAResources(" + buffer.toString() + ")");
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
            _endpoint = _factory.createEndpoint(new MockXAResource());
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
