package org.switchyard.component.camel.netty.deploy;

import javax.xml.namespace.QName;

import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.component.camel.common.deploy.BaseBindingActivator;
import org.switchyard.component.camel.common.handler.InboundHandler;
import org.switchyard.component.camel.common.model.CamelBindingModel;
import org.switchyard.component.camel.netty.model.CamelNettyBindingModel;

/**
 * Camel netty activator.
 */
public class CamelNettyActivator extends BaseBindingActivator {
    /**
     * Creates new activator instance.
     * 
     * @param context Camel context.
     * @param types Activation types.
     */
    public CamelNettyActivator(SwitchYardCamelContext context, String[] types) {
        super(context, types);
    }

    @SuppressWarnings("unchecked")
    protected <T extends CamelBindingModel> InboundHandler<T> createInboundHandler(QName serviceName, T binding) {
        return (InboundHandler<T>) new CamelNettyInboundHandler((CamelNettyBindingModel) binding, getCamelContext(), serviceName, getServiceDomain());
    }

}
