/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.esb.cinco.tests;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.esb.cinco.BaseHandler;
import org.jboss.esb.cinco.Exchange;
import org.jboss.esb.cinco.ExchangeChannel;
import org.jboss.esb.cinco.ExchangeFactory;
import org.jboss.esb.cinco.InOnlyExchange;
import org.jboss.esb.cinco.Message;
import org.jboss.esb.cinco.event.ExchangeCompleteEvent;

public class OneWayConsumer extends BaseHandler {
	
	private ExchangeFactory _exchangeFactory;
	private ExchangeChannel _channel;
	private int _completeCount;
	private int _sendCount;
	private Map<String, Exchange> _activeExchanges = 
		new HashMap<String, Exchange>();
	
	public OneWayConsumer(ExchangeFactory exchangeFactory, 
			ExchangeChannel channel) {
		_exchangeFactory = exchangeFactory;
		_channel = channel;
		_channel.getHandlerChain().addLast("me", this);
	}
	
	public void invokeService(QName serviceName, Message requestMessage) {
		// Create a new InOnly exchange to invoke the service
		InOnlyExchange inOnly = _exchangeFactory.createInOnlyExchange();
		inOnly.setService(serviceName);
		inOnly.setIn(requestMessage);
		
		// Add it to the list of active exchanges
		_activeExchanges.put(inOnly.getId(), inOnly);
		_sendCount++;
		
		// Send the exchange to a service provider
		_channel.send(inOnly);
	}
	
	@Override
	public void exchangeComplete(ExchangeCompleteEvent event) {
		// Remove the exchange from our list of active exchanges
		_activeExchanges.remove(event.getExchange().getId());
		_completeCount++;
	}

	public int getActiveCount() {
		return _activeExchanges.keySet().size();
	}
	
	public int getCompletedCount() {
		return _completeCount;
	}
	
	public int getSendCount() {
		return _sendCount;
	}
}
