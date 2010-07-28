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
import org.jboss.esb.cinco.event.ExchangeInEvent;
import org.jboss.esb.cinco.internal.ExchangeState;

public class BaseProvider extends BaseHandler {
	private ExchangeChannel _channel;
	private int _receiveCount;
	private ExchangeState _nextState;
	private Message _replyMessage;
	
	public BaseProvider(Environment env, ExchangeState nextState) {
		_nextState = nextState;
		_channel = env.getExchangeChannelFactory().createChannel();
		_channel.getHandlerChain().addLast("me", this);
	}
	
	public void provideService(QName serviceName) {
		_channel.registerService(serviceName);
	}
	
	@Override
	public void exchangeIn(ExchangeInEvent event) {
		_receiveCount++;
		
		Exchange exchange = event.getExchange();
		
		switch(_nextState) {
		case OUT :
			exchange.setOut(_replyMessage);
			break;
		case FAULT :
			exchange.setFault(_replyMessage);
			break;
		case ERROR :
			exchange.setError(new Exception());
			break;
		case DONE :
			return;
		}
		
		_channel.send(event.getExchange());
	}
	
	public void setReply(Message message) {
		_replyMessage = message;
	}

	
	public void setNextState(ExchangeState state) {
		_nextState = state;
	}

	public int getReceiveCount() {
		return _receiveCount;
	}
	
}
