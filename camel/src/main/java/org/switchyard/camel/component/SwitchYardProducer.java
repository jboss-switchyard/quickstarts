package org.switchyard.camel.component;

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
    
    private Exchange createSwitchyardExchange(final org.apache.camel.Exchange ex, final ServiceReference serviceRef) {
        return isInOnly(ex.getPattern()) ? createInOnlyExchange(serviceRef) : createInOutExchange(serviceRef, ex);
    }
    
    private boolean isInOnly(final org.apache.camel.ExchangePattern pattern) {
        return pattern == org.apache.camel.ExchangePattern.InOnly;
    }
    
    private Exchange createInOnlyExchange(final ServiceReference serviceReference) {
        final BaseExchangeContract contract = new BaseExchangeContract(new InOnlyOperation(_operationName));
        setOutputType(serviceReference, contract);
        return serviceReference.createExchange(contract);
    }
    
    private Exchange createInOutExchange(final ServiceReference serviceReference, final org.apache.camel.Exchange camelExchange) {
        final BaseExchangeContract contract = new BaseExchangeContract(new InOutOperation(_operationName));
        setOutputType(serviceReference, contract);
        return serviceReference.createExchange(contract, new CamelResponseHandler(camelExchange, serviceReference));
    }
    
    private void setOutputType(final ServiceReference ref, final BaseExchangeContract contract) {
        final QName outputType = ServiceReferences.getOutputTypeForOperation(ref, _operationName);
        if (outputType != null) {
            contract.getInvokerInvocationMetaData().setOutputType(outputType);
        }
    }
    
}
