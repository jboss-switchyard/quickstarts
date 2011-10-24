/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *  *
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

package org.switchyard.component.camel;

import static org.switchyard.component.camel.deploy.ComponentNameComposer.composeSwitchYardServiceName;

import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultProducer;
import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.ServiceReference;
import org.switchyard.component.camel.composer.CamelMessageComposer;
import org.switchyard.component.camel.deploy.ServiceReferences;
import org.switchyard.composer.MessageComposer;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.metadata.BaseExchangeContract;
import org.switchyard.metadata.InOnlyOperation;
import org.switchyard.metadata.InOutOperation;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.metadata.java.JavaService;

/**
 * A Camel producer that is capable of calling SwitchYard services from a Camel route.
 * </p>
 * 
 * A SwitchYardProducer is created by Camel when a 'to' route contains the switchyard component. 
 * For example:
 * <pre>
 *    from("direct://input).
 *    to("switchyard://serviceName?operationName=print");
 * </pre>
 * 
 * @author Daniel Bevenius
 *
 */
public class SwitchYardProducer extends DefaultProducer {
    
    private String _namespace;
    private String _operationName;
    private final MessageComposer<org.apache.camel.Message> _messageComposer;
    
    /**
     * Sole constructor.
     * 
     * @param endpoint the Camel Endpoint that this Producer belongs to.
     * @param namespace the service namespace of the target SwitchYard service.
     * @param operationName the operation name of the target SwitchYard service.
     * @param messageComposer the MessageComposer to use
     */
    public SwitchYardProducer(final Endpoint endpoint, final String namespace, final String operationName, final MessageComposer<org.apache.camel.Message> messageComposer) {
        super(endpoint);
        _namespace = namespace;
        _operationName = operationName;
        _messageComposer = messageComposer;
    }
    
    @Override
    public void process(final org.apache.camel.Exchange camelExchange) throws Exception {
        final String targetUri = (String) camelExchange.getProperty("CamelToEndpoint");
        final ServiceReference serviceRef = lookupServiceReference(targetUri);
        if (_operationName == null) {
            _operationName = lookupOperationNameFor(serviceRef);
        }
        final Exchange switchyardExchange = createSwitchyardExchange(camelExchange, serviceRef);
        final SwitchYardProducer producer = this;
        final CamelMessageComposer.ContentTypeProvider ctp = new CamelMessageComposer.ContentTypeProvider() {
            public Class<?> getContentType() {
                return producer.getInputType(serviceRef);
            }
        };
        try {
            CamelMessageComposer.setContentTypeProvider(switchyardExchange, ctp);
            Message switchyardMessage = _messageComposer.compose(camelExchange.getIn(), switchyardExchange, true);
            switchyardExchange.send(switchyardMessage);
        } finally {
            CamelMessageComposer.setContentTypeProvider(switchyardExchange, null);
        }
    }
    
    private ServiceReference lookupServiceReference(final String targetUri) {
        final QName serviceName = composeSwitchYardServiceName(_namespace, targetUri);
        final ServiceReference serviceRef = ServiceReferences.get(serviceName);
        if (serviceRef == null) {
            throw new NullPointerException("No ServiceReference was found for uri [" + targetUri + "]");
        }
        return serviceRef;
    }
    
    private Exchange createSwitchyardExchange(final org.apache.camel.Exchange camelExchange, final ServiceReference serviceRef) {
        return isInOnly(camelExchange.getPattern()) ? createInOnlyExchange(serviceRef, camelExchange) : createInOutExchange(serviceRef, camelExchange);
    }
    
    private boolean isInOnly(final org.apache.camel.ExchangePattern pattern) {
        return pattern == org.apache.camel.ExchangePattern.InOnly;
    }
    
    private Exchange createInOnlyExchange(final ServiceReference serviceReference, final org.apache.camel.Exchange ex) {
        final QName operationInputType = getOperationInputType(serviceReference);
        final InOnlyOperation inOnlyOperation = new InOnlyOperation(_operationName, operationInputType);
        final BaseExchangeContract contract = new BaseExchangeContract(inOnlyOperation);
        setInputMessageType(contract, operationInputType);
        
        return serviceReference.createExchange(contract);
    }
    
    private String lookupOperationNameFor(final ServiceReference serviceRef) {
        final Set<ServiceOperation> operations = serviceRef.getInterface().getOperations();
        if (operations.size() != 1) {
            final StringBuilder msg = new StringBuilder();
            msg.append("No operationSelector was configured for the Camel Component and the Service Interface ");
            msg.append("contains more than one operation: ").append(operations);
            msg.append("Please add an operationSelector element with the target 'operationName' as an attribute.");
            throw new SwitchYardException(msg.toString());
        }
        final ServiceOperation serviceOperation = operations.iterator().next();
        return serviceOperation.getName();
    }
    
    private Exchange createInOutExchange(final ServiceReference ref, final org.apache.camel.Exchange camelExchange) {
        final QName operationInputType = getOperationInputType(ref);
        final QName operationOutputType = getOperationOutputType(ref);
        final InOutOperation inOutOperation = new InOutOperation(_operationName, operationInputType, operationOutputType);
        final BaseExchangeContract exchangeContract = new BaseExchangeContract(inOutOperation);
        setInputMessageType(exchangeContract, operationInputType);
        
        return ref.createExchange(exchangeContract, new CamelResponseHandler(camelExchange, ref, _messageComposer));
    }
    
    private QName getOperationInputType(final ServiceReference ref) {
        final ServiceOperation operation = getOperation(ref);
        if (operation != null) {
            return operation.getInputType();
        }
        return null;
    }
    
    private QName getOperationOutputType(final ServiceReference ref) {
        final ServiceOperation operation = getOperation(ref);
        if (operation != null) {
            return operation.getOutputType();
        }
        return null;
    }
    
    private ServiceOperation getOperation(final ServiceReference ref) {
        return ref.getInterface().getOperation(_operationName);
    }
    
    private void setInputMessageType(final BaseExchangeContract exchangeContract, final QName type) {
        exchangeContract.getInvokerInvocationMetaData().setInputType(type);
    }

    private Class<?> getInputType(final ServiceReference serviceRef) {
        final QName inputType = getOperationInputType(serviceRef);
        if (inputType != null) {
            return JavaService.parseType(inputType);
        }
        return Object.class;
    }
    
}
