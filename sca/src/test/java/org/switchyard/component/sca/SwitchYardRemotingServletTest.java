package org.switchyard.component.sca;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.Exchange;
import org.switchyard.ServiceDomain;
import org.switchyard.component.sca.RemoteEndpointPublisher;
import org.switchyard.component.sca.SwitchYardRemotingServlet;
import org.switchyard.deploy.internal.Deployment;
import org.switchyard.internal.DomainImpl;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.java.JavaService;
import org.switchyard.remote.RemoteMessage;
import org.switchyard.remote.http.HttpInvoker;
import org.switchyard.serial.FormatType;
import org.switchyard.serial.Serializer;
import org.switchyard.serial.SerializerFactory;
import org.switchyard.test.MockHandler;

public class SwitchYardRemotingServletTest {
    
    private final QName TEST_SERVICE = new QName("RemotingTest");
    
    private Serializer serializer = SerializerFactory.create(FormatType.JSON, null, true);
    private SwitchYardRemotingServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServiceDomain domain;
    private InputStream input;
    
    @Before
    public void setUp() throws Exception {
        domain = new DomainImpl(new QName("test"));
        domain.getProperties().put(Deployment.CLASSLOADER_PROPERTY, this.getClass().getClassLoader());
        RemoteEndpointPublisher rep = mock(RemoteEndpointPublisher.class);
        when(rep.getDomain(TEST_SERVICE)).thenReturn(domain);
        servlet = new SwitchYardRemotingServlet();
        servlet.setEndpointPublisher(rep);
        request = mock(HttpServletRequest.class);
        ServletInputStream sis = new ServletInputStream() {
            public int read() throws IOException {
                return input.read();
            }
            public int read(byte[] b, int off, int len) throws IOException {
                return input.read(b, off, len);
            }
        };
        when(request.getInputStream()).thenReturn(sis);
        when(request.getHeader(HttpInvoker.SERVICE_HEADER)).thenReturn(TEST_SERVICE.toString());
        response = mock(HttpServletResponse.class);
    }

    @Test
    public void missingServiceName() {
        try {
            when(request.getHeader(HttpInvoker.SERVICE_HEADER)).thenReturn(null);
            servlet.doPost(request, response);
            Assert.fail("Expected failure due to missing service name on message");
        } catch (Exception ex) {
            // success
        }
    }
    
    @Test
    public void noOperationName() throws Exception {
        domain.registerServiceReference(TEST_SERVICE, new InOnlyService());
        domain.registerService(TEST_SERVICE, new InOnlyService(), new MockHandler());
        RemoteMessage msg = new RemoteMessage()
            .setService(TEST_SERVICE);
        setRequestMessage(msg);
        servlet.doPost(request, response);
    }
    
    @Test
    public void noOperationNameMultipleOperations() throws Exception {
        MockHandler handler = new MockHandler();
        domain.registerServiceReference(TEST_SERVICE, JavaService.fromClass(MyInterface.class));
        domain.registerService(TEST_SERVICE, JavaService.fromClass(MyInterface.class), handler);
        
        RemoteMessage msg = new RemoteMessage()
            .setService(TEST_SERVICE);
        setRequestMessage(msg);
        
        try {
            servlet.doPost(request, response);
            Assert.fail("No operation was supplied with multiple service operations - this should fail!");
        } catch (Exception ex) {
            // expected
        }
    }
    
    @Test
    public void operationNameMultipleOperations() throws Exception {
        MockHandler handler = new MockHandler();
        domain.registerServiceReference(TEST_SERVICE, JavaService.fromClass(MyInterface.class));
        domain.registerService(TEST_SERVICE, JavaService.fromClass(MyInterface.class), handler);
        
        RemoteMessage msg = new RemoteMessage()
            .setService(TEST_SERVICE)
            .setOperation("bar");
        setRequestMessage(msg);
        servlet.doPost(request, response);
        
        Exchange ex = handler.getMessages().poll(300, TimeUnit.MILLISECONDS);
        Assert.assertNotNull(ex);
        Assert.assertEquals("bar", ex.getContract().getConsumerOperation().getName());
    }
    
    private void setRequestMessage(RemoteMessage message) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        serializer.serialize(message, RemoteMessage.class, bos);
        bos.flush();
        input = new ByteArrayInputStream(bos.toByteArray());
    }
    
}

interface MyInterface {
    void foo(String arg);
    void bar(String arg);
}
