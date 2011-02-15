/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard.component.soap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.namespace.QName;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.Message;
import org.switchyard.ServiceDomain;
import org.switchyard.SwitchYardTestCase;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.component.soap.util.SOAPUtil;
import org.switchyard.config.model.ModelResource;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.metadata.BaseService;
import org.switchyard.metadata.InOnlyOperation;
import org.switchyard.metadata.InOutOperation;
import org.switchyard.metadata.ServiceOperation;
import org.w3c.dom.Element;

/**
 * Contains tests for SOAPGateway.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class SOAPGatewayTest extends SwitchYardTestCase {
    private static final QName WS_CONSUMER_SERVICE = new QName("webservice-consumer");
    private static final int DEFAULT_THREAD_COUNT = 10;
    private static final long DEFAULT_NO_OF_THREADS = 100;

    private static URL _serviceURL;
    private ServiceDomain _domain;
    private SOAPGateway _soapInbound;
    private SOAPGateway _soapOutbound;
    private long _noOfThreads = DEFAULT_NO_OF_THREADS;
    
    private static ModelResource _res;

    private class WebServiceInvoker implements Callable<String> {

        private long _threadNo;

        public WebServiceInvoker(long threadNo) {
            _threadNo = threadNo;
        }

        public String call() {
            String input = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body>"
                     + "   <test:sayHello xmlns:test=\"http://test.ws/\">"
                     + "      <arg0>Thread " + _threadNo + "</arg0>"
                     + "   </test:sayHello>"
                     + "</soap:Body></soap:Envelope>";
            String output = null;

            try {
                HttpURLConnection con = (HttpURLConnection) _serviceURL.openConnection();
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setRequestProperty("Content-type", "text/xml; charset=utf-8");
                OutputStream outStream = con.getOutputStream();
                outStream.write(input.getBytes());
                InputStream inStream = con.getInputStream();
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                byte[] byteBuf = new byte[256];
                int len = inStream.read(byteBuf);
                while (len > -1) {
                    byteStream.write(byteBuf, 0, len);
                    len = inStream.read(byteBuf);
                }
                outStream.close();
                inStream.close();
                byteStream.close();
                output =  byteStream.toString();

            } catch (IOException ioe) {
                output = "<error>" + ioe + "</error>";
            }
            return output;
        }
    }

    @Before
    public void setUp() throws Exception {
        _res = new ModelResource();
        
        // Provide a switchyard service
        _domain = getServiceDomain();
        SOAPProvider provider = new SOAPProvider();

        CompositeModel composite = (CompositeModel)_res.pull("/HelloSwitchYard.xml");
        /*
        Validation v = composite.validateModel();
        if (!v.isValid()) {
            System.err.println("CompositeModel not valid: " + v.getMessage());
            v.getCause().printStackTrace();
        }
        */

        CompositeServiceModel compositeService = composite.getServices().get(0);
        SOAPBindingModel config = (SOAPBindingModel)compositeService.getBindings().get(0);

        _domain.registerService(config.getServiceName(), provider, new HelloWebServiceInterface());

        _domain.getTransformerRegistry().addTransformer(new HandlerExceptionTransformer());

        String host = System.getProperty("org.switchyard.test.soap.host", "localhost");
        String port = System.getProperty("org.switchyard.test.soap.port", "48080");

        // Service exposed as WS
        _soapInbound = new SOAPGateway();

        config.setPublishAsWS(true);
        config.setServerHost(host);
        config.setServerPort(Integer.parseInt(port));
        _soapInbound.init(config, _domain);

        _soapInbound.start();

        _serviceURL = new URL("http://" + host + ":" + port + "/HelloWebService");

        // A WS Consumer as Service
        _soapOutbound = new SOAPGateway();
        config = new SOAPBindingModel();
        config.setWsdl(_serviceURL.toExternalForm() + "?wsdl");
        config.setServiceName(WS_CONSUMER_SERVICE);
        _soapOutbound.init(config, _domain);
        _soapOutbound.start();
        
        XMLUnit.setIgnoreWhitespace(true);
    }
    
    @After
    public void tearDown() throws Exception {
        _soapOutbound.stop();
        _soapInbound.stop();
        _soapInbound.destroy();
        _soapOutbound.destroy();
    }

    @Test
    public void invokeOneWay() throws Exception {
        Element input = SOAPUtil.parseAsDom("<!--Comment --><test:helloWS xmlns:test=\"http://test.ws/\">"
                     + "   <arg0>Hello</arg0>"
                     + "</test:helloWS>").getDocumentElement();

        newInvoker(WS_CONSUMER_SERVICE).sendInOnly(input);
    }

    @Test
    public void invokeRequestResponse() throws Exception {
        String input = "<test:sayHello xmlns:test=\"http://test.ws/\">"
                     + "   <arg0>Jimbo</arg0>"
                     + "</test:sayHello>";

        String output = "<test:sayHelloResponse xmlns:test=\"http://test.ws/\">"
                     + "   <return>Hello Jimbo</return>"
                     + "</test:sayHelloResponse>";


        Message responseMsg = newInvoker(WS_CONSUMER_SERVICE).sendInOut(input);

        String response = toString(responseMsg.getContent(Element.class));
        XMLAssert.assertXMLEqual(output, response);
    }

    private String toString(Element element) throws Exception
    {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        StringWriter sw = new StringWriter();
        DOMSource source = new DOMSource(element);
        StreamResult result = new StreamResult(sw);
        transformer.transform(source, result);
        return sw.toString();
    }

    @Test
    public void invokeRequestResponseFault() throws Exception {
        String input = "<test:sayHello xmlns:test=\"http://test.ws/\">"
                     + "   <arg0></arg0>"
                     + "</test:sayHello>";

        String output = "<soap:Fault xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                        + "   <faultcode>soap:Server.AppError</faultcode>"
                        + "   <faultstring>Invalid name</faultstring>"
                        + "   <detail>"
                        + "      <message>Looks like you did not specify a name!</message>"
                        + "      <errorcode>1000</errorcode>"
                        + "   </detail>"
                        + "</soap:Fault>";

        Message responseMsg = newInvoker(WS_CONSUMER_SERVICE).sendInOut(input);
        String response = toString(responseMsg.getContent(Element.class));
        XMLAssert.assertXMLEqual(output, response);
    }

    @Test
    public void invokeMultiThreaded() throws Exception {
        String output = null;
        String response = null;
        Collection<Callable<String>> callables = new ArrayList<Callable<String>>();
        for (int i = 0; i < _noOfThreads; i++) {
            callables.add(new WebServiceInvoker(i));
        }

        ExecutorService executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_COUNT);
        Collection<Future<String>> futures = executorService.invokeAll(callables);
        Assert.assertEquals(futures.size(), _noOfThreads);
        int i = 0;

        for (Future<String> future : futures) {
            response = future.get();
            output =  "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body>"
                     + "   <test:sayHelloResponse xmlns:test=\"http://test.ws/\">"
                     + "      <return>Hello Thread " + i + "</return>"
                     + "   </test:sayHelloResponse>"
                     + "</soap:Body></soap:Envelope>";
            XMLAssert.assertXMLEqual(output, response);
            i++;
        }
    }

    private static class HelloWebServiceInterface extends BaseService {
        private static Set<ServiceOperation> _operations = new HashSet<ServiceOperation>(2);
        static {
            _operations.add(new InOutOperation("sayHello"));
            _operations.add(new InOnlyOperation("helloWS"));
        }
        public HelloWebServiceInterface() {
            super(_operations);
        }
    }
}

