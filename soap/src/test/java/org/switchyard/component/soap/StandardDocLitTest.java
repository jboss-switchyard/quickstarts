/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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

package org.switchyard.component.soap;

import java.io.StringWriter;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Message;
import org.switchyard.ServiceDomain;
import org.switchyard.common.net.SocketAddr;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.component.soap.config.model.v1.V1SOAPBindingModel;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.metadata.BaseService;
import org.switchyard.metadata.InOutOperation;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.test.Invoker;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestKit;
import org.w3c.dom.Node;

/**
 * Contains tests for standard doclit (non-wrapped doclit).
 *
 * @author Mario Antollini
 */
@RunWith(SwitchYardRunner.class)
public class StandardDocLitTest {

    private SwitchYardTestKit _testKit;
    
    private ServiceDomain _domain = new ServiceDomainManager().createDomain();
    private SOAPBindingModel _config;
    private static URL _serviceURL;
    private InboundHandler _soapInbound;
    private OutboundHandler _soapOutbound;
    
    @org.switchyard.test.ServiceOperation("webservice-consumer")
    private Invoker consumerService;

    private static ModelPuller<CompositeModel> _puller;
    
    @Before
    public void setUp() throws Exception {
        
        // Provide a switchyard service
        DoclitSOAPProvider provider = new DoclitSOAPProvider();

        String host = System.getProperty("org.switchyard.test.soap.host", "localhost");
        String port = System.getProperty("org.switchyard.test.soap.port", "48080");

        _puller = new ModelPuller<CompositeModel>();
        CompositeModel composite = _puller.pull("/DoclitSwitchyard.xml", getClass());
        
        CompositeServiceModel compositeService = composite.getServices().get(0);
        _config = (SOAPBindingModel)compositeService.getBindings().get(0);

        _domain.registerService(_config.getServiceName(), new OrderServiceInterface(), provider);
        _domain.registerServiceReference(_config.getServiceName(), new OrderServiceInterface());
        
        _config.setSocketAddr(new SocketAddr(host, Integer.parseInt(port)));
        
        _soapInbound = new InboundHandler(_config, _domain);

        _soapInbound.start();

        _serviceURL = new URL("http://" + host + ":" + port + "/OrderService");

        // A WS Consumer as Service
        SOAPBindingModel config2 = new V1SOAPBindingModel();
        config2.setWsdl(_serviceURL.toExternalForm() + "?wsdl");
        config2.setServiceName(consumerService.getServiceName());
        _soapOutbound = new OutboundHandler(config2);
        _soapOutbound.start();
        _domain.registerService(consumerService.getServiceName(), new OrderServiceInterface(), _soapOutbound);
        
        XMLUnit.setIgnoreWhitespace(true);
    }

    @After
    public void tearDown() throws Exception {
        // NOOP
    }

    @Test
    public void standardDocLitOperation() throws Exception {
        
        DOMSource domSource = new DOMSource(_testKit.readResourceDocument("/doclit_request.xml"));
        Message responseMsg = consumerService.operation("submitOrder").sendInOut(toString(domSource.getNode()));
        String response = toString(responseMsg.getContent(Node.class));
        _testKit.compareXMLToResource(response, "/doclit_response.xml");
        
    }
    
    private static class OrderServiceInterface extends BaseService {
        private static Set<ServiceOperation> _operations = new HashSet<ServiceOperation>(2);
        static {
            _operations.add(new InOutOperation("submitOrder"));
        }
        public OrderServiceInterface() {
            super(_operations);
        }
    }
    
    private String toString(Node node) throws Exception
    {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        StringWriter sw = new StringWriter();
        DOMSource source = new DOMSource(node);
        StreamResult result = new StreamResult(sw);
        transformer.transform(source, result);
        return sw.toString();
    }
    
}
