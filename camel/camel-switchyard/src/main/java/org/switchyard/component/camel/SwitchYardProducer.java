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

import static org.switchyard.component.camel.ComponentNameComposer.composeSwitchYardServiceName;

import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultProducer;
import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.ExchangePhase;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.component.camel.common.CamelConstants;
import org.switchyard.component.camel.common.composer.BindingDataCreator;
import org.switchyard.component.camel.common.composer.BindingDataCreatorResolver;
import org.switchyard.component.camel.common.composer.CamelBindingData;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.common.composer.SecurityBindingData;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.label.BehaviorLabel;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.policy.PolicyUtil;
import org.switchyard.policy.TransactionPolicy;
import org.switchyard.runtime.event.ExchangeCompletionEvent;
import org.switchyard.security.SecurityContext;
import org.switchyard.selector.OperationSelector;

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

    private final static Logger LOG = Logger.getLogger(SwitchYardProducer.class);
    private String _namespace;
    private String _operationName;
    private final MessageComposer<CamelBindingData> _messageComposer;

    /**
     * Sole constructor.
     * 
     * @param endpoint the Camel Endpoint that this Producer belongs to.
     * @param namespace the service namespace of the target SwitchYard service.
     * @param operationName the operation name of the target SwitchYard service.
     * @param messageComposer the MessageComposer to use
     */
    public SwitchYardProducer(final Endpoint endpoint, final String namespace, final String operationName, final MessageComposer<CamelBindingData> messageComposer) {
        super(endpoint);
        _namespace = namespace;
        _operationName = operationName;
        _messageComposer = messageComposer;
    }

    @Override
    public void process(final org.apache.camel.Exchange camelExchange) throws Exception {
        final String targetUri = camelExchange.getProperty(org.apache.camel.Exchange.TO_ENDPOINT, String.class);
        ServiceDomain domain = ((SwitchYardCamelContext) camelExchange.getContext()).getServiceDomain();
        final ServiceReference serviceRef = lookupServiceReference(targetUri, domain);

        // set a flag to indicate whether this producer endpoint is used within a service route
        boolean isGatewayRoute = camelExchange.getProperty(SwitchYardConsumer.IMPLEMENTATION_ROUTE) == null;
        
        // the composer is not used for switchyard:// endpoints invoked from service routes
        MessageComposer<CamelBindingData> composer = 
                isGatewayRoute ? getMessageComposer(camelExchange) : null;
        final Exchange switchyardExchange = createSwitchyardExchange(camelExchange, serviceRef, composer);

        // Set appropriate policy based on Camel exchange properties
        if (camelExchange.isTransacted()) {
            PolicyUtil.provide(switchyardExchange, TransactionPolicy.PROPAGATES_TRANSACTION);
            PolicyUtil.provide(switchyardExchange, TransactionPolicy.MANAGED_TRANSACTION_GLOBAL);
        }
        
        // Message composition depends on whether this switchyard:// endpoint is called from
        // a Camel service implementation or a Camel gateway
        Message switchyardMessage;
        if (isGatewayRoute) {
            switchyardMessage = composeForGateway(composer, camelExchange, switchyardExchange);
        } else {
            switchyardMessage = ExchangeMapper.mapCamelToSwitchYard(
                    camelExchange, switchyardExchange, ExchangePhase.IN);
        }
        
        switchyardExchange.send(switchyardMessage);
    }
    
    private Message composeForGateway(MessageComposer<CamelBindingData> composer, 
            org.apache.camel.Exchange camelExchange, Exchange switchyardExchange) throws Exception {
        
        BindingDataCreator<?> bindingCreator = getBindingDataCreator(camelExchange);
        CamelBindingData bindingData = bindingCreator.createBindingData(camelExchange.getIn());
        if (bindingData instanceof SecurityBindingData) {
            // returned binding is contains some security bindings, let's move them to security context
            SecurityContext.get(switchyardExchange).getCredentials().addAll(
                ((SecurityBindingData) bindingData).extractCredentials());
        }
        
        /*
         * initialize the gateway name on the context. this was most likely not
         * mapped by the message composer/context mapper.
         */
        final String gatewayName = camelExchange.getProperty(ExchangeCompletionEvent.GATEWAY_NAME, String.class);
        if (gatewayName != null) {
            switchyardExchange.getContext()
                    .setProperty(ExchangeCompletionEvent.GATEWAY_NAME, gatewayName, Scope.EXCHANGE)
                    .addLabels(BehaviorLabel.TRANSIENT.label());
        }
        
        return composer.compose(bindingData, switchyardExchange);
    }

    /**
     * Helper method which lookup for BindingDataCreatorResolver and uses returned
     * instance to create new CamelBindingData.
     * 
     * @param camelExchange Camel exchange.
     * @return Binding data creator.
     */
    private BindingDataCreator<?> getBindingDataCreator(org.apache.camel.Exchange camelExchange) {
        BindingDataCreatorResolver resolver = ((SwitchYardEndpoint) getEndpoint()).getBindingDataCreatorResolver();
        String resolverKey = camelExchange.getFromEndpoint().getClass().getSimpleName();
        return resolver.resolveBindingCreator(resolverKey, getEndpoint().getCamelContext());
    }

    @SuppressWarnings("unchecked")
    private String getOperationName(org.apache.camel.Exchange exchange) {
        String operationName = null;
        
        OperationSelector<CamelBindingData> selector = exchange.getIn().getHeader(CamelConstants.OPERATION_SELECTOR_HEADER, OperationSelector.class);
        if (selector != null) {
            try {
                operationName = selector.selectOperation(new CamelBindingData(exchange.getIn())).getLocalPart();
            } catch (Exception e) {
                LOG.error("Cannot lookup operation using custom operation selector. Returning empty name", e);
            }
        }
        return operationName;
    }

    @SuppressWarnings("unchecked")
    private MessageComposer<CamelBindingData> getMessageComposer(org.apache.camel.Exchange exchange) {
        MessageComposer<CamelBindingData> composer = exchange.getIn().getHeader(CamelConstants.MESSAGE_COMPOSER_HEADER, MessageComposer.class);
        return composer == null ? _messageComposer : composer;
    }

    private ServiceReference lookupServiceReference(final String targetUri, ServiceDomain domain) {
        final QName serviceName = composeSwitchYardServiceName(_namespace, targetUri);
        final ServiceReference serviceRef = domain.getServiceReference(serviceName);
        if (serviceRef == null) {
            throw new NullPointerException("No ServiceReference was found for uri [" + targetUri + "]");
        }
        return serviceRef;
    }

    private Exchange createSwitchyardExchange(final org.apache.camel.Exchange camelExchange, final ServiceReference serviceRef,
        MessageComposer<CamelBindingData> messageComposer) {
        String opName = lookupOperationNameFor(camelExchange, serviceRef);
        CamelResponseHandler handler = new CamelResponseHandler(camelExchange, serviceRef, messageComposer);

        if (opName != null) {
            return serviceRef.createExchange(opName, handler);
        } else {
            return serviceRef.createExchange(handler);
        }
    }

    private String lookupOperationNameFor(final org.apache.camel.Exchange camelExchange, final ServiceReference serviceRef) {
        
        // Initialize operation name to whatever is specified in endpoint URI
        String operationName = _operationName;
        
        // See if an operation selector has been specified
        if (operationName == null) {
            operationName = getOperationName(camelExchange);
        }
        
        // If we still haven't found an operation and the target service only has one operation, 
        // then just use that
        if (operationName == null) {
            Set<ServiceOperation> ops = serviceRef.getInterface().getOperations();
            if (ops.size() == 1) {
                operationName = ops.iterator().next().getName();
            } else {
                // See if the existing camel operation exists on the target service
                String camelOp = camelExchange.getProperty(Exchange.OPERATION_NAME, String.class);
                if (serviceRef.getInterface().getOperation(camelOp) != null) {
                    operationName = camelOp;
                }
            }
        }
        
        // Still haven't found it?  Houston, we have a problem.
        if (operationName == null) {
            final StringBuilder msg = new StringBuilder();
            msg.append("Unable to determine operation as the target service contains more than one operation");
            msg.append(serviceRef.getInterface().getOperations());
            throw new SwitchYardException(msg.toString());
        }
        return operationName;
    }

}
