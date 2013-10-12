package org.switchyard.component.camel.netty.deploy;

import javax.xml.namespace.QName;

import org.apache.camel.Route;
import org.apache.camel.component.netty.NettyConsumer;
import org.jboss.netty.channel.socket.DatagramChannelFactory;
import org.switchyard.ServiceDomain;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.component.camel.common.handler.InboundHandler;
import org.switchyard.component.camel.netty.model.CamelNettyBindingModel;


/**
 * Inbound handler for Netty binding. Used to cleanly shutdown the DatagramChannelFactory.
 * This fixes SwitchYard-1633 and can be remove once Camel 2.10.8 is released.
 */
public class CamelNettyInboundHandler extends InboundHandler<CamelNettyBindingModel> {

    /**
     * Sole constructor.
     * 
     * @param camelBindingModel The CamelBindingModel.
     * @param camelContext The camel context instance.
     * @param serviceName The target service name.
     * @param domain the service domain.
     */
    public CamelNettyInboundHandler(CamelNettyBindingModel camelBindingModel,
        SwitchYardCamelContext camelContext, QName serviceName, ServiceDomain domain) {
        super(camelBindingModel, camelContext, serviceName, domain);
    }

    @Override
    protected void doStop() {
        String routeId = this.getRouteId();
        SwitchYardCamelContext context = this.getSwitchYardCamelContext();
        Route route = context.getRoute(routeId);
        NettyConsumer consumer = (NettyConsumer)route.getConsumer();
        DatagramChannelFactory datagramChannelFactory = consumer.getDatagramChannelFactory();
        if (datagramChannelFactory != null) {
            datagramChannelFactory.releaseExternalResources();
        }
        super.doStop();
    }

}
