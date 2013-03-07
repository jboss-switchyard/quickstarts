package org.switchyard.component.sca;

import javax.xml.namespace.QName;

import org.switchyard.ServiceDomain;

/**
 * This is a dummy implementation of RemoteEndpointPublisher that exists solely to satisfy
 * the publisher service requirement in unit tests.  The SY JBoss AS extension has a real 
 * implementation of this contract.
 */
public class NOPEndpointPublisher implements RemoteEndpointPublisher {

    @Override
    public void init(String context) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void start() throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void stop() throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addService(QName serviceName, ServiceDomain domain) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeService(QName serviceName, ServiceDomain domain) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public ServiceDomain getDomain(QName serviceName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getAddress() {
        // TODO Auto-generated method stub
        return null;
    }

}
