
package org.switchyard.quickstarts.bpm.service.data;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.switchyard.demo.openshift package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _OrderAck_QNAME = new QName("urn:switchyard-quickstart:bpm-service:1.0", "submitOrderResponse");
    private final static QName _Order_QNAME = new QName("urn:switchyard-quickstart:bpm-service:1.0", "submitOrder");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.switchyard.demo.openshift
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Order }
     * 
     */
    public Order createOrder() {
        return new Order();
    }

    /**
     * Create an instance of {@link OrderAck }
     * 
     */
    public OrderAck createOrderAck() {
        return new OrderAck();
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OrderAck }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:switchyard-quickstart:bpm-service:1.0", name = "submitOrderResponse")
    public JAXBElement<OrderAck> createOrderAck(OrderAck value) {
        return new JAXBElement<OrderAck>(_OrderAck_QNAME, OrderAck.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Order }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:switchyard-quickstart:bpm-service:1.0", name = "submitOrder")
    public JAXBElement<Order> createOrder(Order value) {
        return new JAXBElement<Order>(_Order_QNAME, Order.class, null, value);
    }
 
}
