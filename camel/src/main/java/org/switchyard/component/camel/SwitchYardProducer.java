package org.switchyard.component.camel;

import static org.switchyard.component.camel.deploy.ComponentNameComposer.componseSwitchYardServiceName;

import javax.xml.namespace.QName;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultProducer;
import org.switchyard.Exchange;
import org.switchyard.ServiceReference;
import org.switchyard.component.camel.deploy.ServiceReferences;
import org.switchyard.metadata.BaseExchangeContract;
import org.switchyard.metadata.InOnlyOperation;
import org.switchyard.metadata.InOutOperation;
import org.switchyard.metadata.ServiceInterface;
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
    
    private String _operationName;
    
    /**
     * Sole constructor.
     * 
     * @param endpoint the Camel Endpoint that this Producer belongs to.
     * @param operationName the operation name of the target SwitchYard servcie.
     */
    public SwitchYardProducer(final Endpoint endpoint, final String operationName) {
        super(endpoint);
        _operationName = operationName;
    }
    
    @Override
    public void process(final org.apache.camel.Exchange camelExchange) throws Exception {
        final String targetUri = (String) camelExchange.getProperty("CamelToEndpoint");
        final ServiceReference serviceRef = lookupServiceReference(targetUri);
        final Exchange switchyardExchange = createSwitchyardExchange(camelExchange, serviceRef);
        
        final Object camelPayload = camelExchange.getIn().getBody();
        final org.switchyard.Message switchyardMsg = switchyardExchange.createMessage().setContent(camelPayload);
        switchyardExchange.send(switchyardMsg);
    }
    
    private ServiceReference lookupServiceReference(final String targetUri) {
        final ServiceReference serviceRef = ServiceReferences.get(componseSwitchYardServiceName(targetUri));
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
        setInputMessageType(contract, getCamelBodyType(ex));
        
        return serviceReference.createExchange(contract);
    }
    
    private Exchange createInOutExchange(final ServiceReference ref, final org.apache.camel.Exchange camelExchange) {
        final QName operationInputType = getOperationInputType(ref);
        final QName operationOutputType = getOperationOutputType(ref);
        final InOutOperation inOutOperation = new InOutOperation(_operationName, operationInputType, operationOutputType);
        final BaseExchangeContract exchangeContract = new BaseExchangeContract(inOutOperation);
        setInputMessageType(exchangeContract, getCamelBodyType(camelExchange));
        
        return ref.createExchange(exchangeContract, new CamelResponseHandler(camelExchange, ref));
    }
    
    private QName getOperationInputType(final ServiceReference ref) {
        final ServiceOperation operation = ref.getInterface().getOperation(_operationName);
        if (operation != null) {
            return operation.getInputType();
        }
        return null;
    }
    
    private QName getOperationOutputType(final ServiceReference ref) {
        final ServiceInterface serviceInterface = ref.getInterface();
        final ServiceOperation operation = serviceInterface.getOperation(_operationName);
        if (operation != null) {
            return operation.getOutputType();
        }
        return null;
    }
    
    private void setInputMessageType(final BaseExchangeContract exchangeContract, final Class<?> type) {
        exchangeContract.getInvokerInvocationMetaData().setInputType(JavaService.toMessageType(type));
    }
    
    private Class<?> getCamelBodyType(final org.apache.camel.Exchange exchange) {
        final Object camelPayload = exchange.getIn().getBody();
        if (camelPayload == null) {
            return null;
        }
        
        return camelPayload.getClass();
    }
    
}
