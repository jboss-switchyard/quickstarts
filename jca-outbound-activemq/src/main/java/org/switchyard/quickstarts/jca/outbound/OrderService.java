package org.switchyard.quickstarts.jca.outbound;

/**
 * OrderService.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public interface OrderService {
    /**
     * Entry an order.
     * 
     * @param order order
     */
    public void process(String order);
}
