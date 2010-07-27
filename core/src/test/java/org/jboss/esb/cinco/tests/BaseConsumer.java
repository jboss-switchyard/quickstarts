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

import javax.xml.namespace.QName;

import org.jboss.esb.cinco.BaseHandler;
import org.jboss.esb.cinco.Exchange;
import org.jboss.esb.cinco.ExchangeChannel;
import org.jboss.esb.cinco.Message;
import org.jboss.esb.cinco.event.ExchangeErrorEvent;
import org.jboss.esb.cinco.event.ExchangeFaultEvent;
import org.jboss.esb.cinco.event.ExchangeOutEvent;
import org.jboss.esb.cinco.internal.Environment;

public class BaseConsumer extends BaseHandler {
	
	private ExchangeChannel _channel;
	private int _errorCount;
	private int _faultCount;
	private int _outCount;
	private int _sendCount;
	
	public BaseConsumer(Environment env) {
		_channel = env.getExchangeChannelFactory().createChannel();
		_channel.getHandlerChain().addLast("me", this);
	}
	
	public void invokeService(
			Exchange exchange, QName serviceName, Message requestMessage) {
		
		exchange.setService(serviceName);
		exchange.setIn(requestMessage);
		
		_sendCount++;
		
		// Send the exchange to a service provider
		_channel.send(exchange);
	}
	
	public void exchangeOut(ExchangeOutEvent event) {
		_outCount++;
	}
	
	public void exchangeFault(ExchangeFaultEvent event) {
		_faultCount++;
	}
	
	public void exchangeError(ExchangeErrorEvent event) {
		_errorCount++;
	}
	
	public int getFaultCount() {
		return _faultCount;
	}

	public int getErrorCount() {
		return _errorCount;
	}

	public int getOutCount() {
		return _outCount;
	}
	
	public int getSendCount() {
		return _sendCount;
	}
}
