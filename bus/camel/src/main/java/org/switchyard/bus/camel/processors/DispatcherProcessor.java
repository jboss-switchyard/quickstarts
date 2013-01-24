/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.bus.camel.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.switchyard.bus.camel.CamelHelper;

/**
 * Processor which wraps SwitchYard _exchange into Camel _exchange.
 */
public class DispatcherProcessor implements Processor {

    private final org.switchyard.Exchange _exchange;

    /**
     * Creates new processor which sends SwitchYard exchange over camel route.
     * 
     * @param exchange SwitchYard exchange.
     */
    public DispatcherProcessor(org.switchyard.Exchange exchange) {
        this._exchange = exchange;
    }

    @Override
    public void process(Exchange ex) throws Exception {
        CamelHelper.setSwitchYardExchange(ex, _exchange);
        ex.getIn().setBody(_exchange.getMessage().getContent());
    }

}
