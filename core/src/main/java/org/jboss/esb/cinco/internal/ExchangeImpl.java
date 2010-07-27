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

package org.jboss.esb.cinco.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.jboss.esb.cinco.Exchange;
import org.jboss.esb.cinco.ExchangeContext;
import org.jboss.esb.cinco.ExchangePattern;
import org.jboss.esb.cinco.Message;
import org.jboss.esb.cinco.spi.ExchangeEndpoint;

public class ExchangeImpl implements Exchange {
	
	public static final String IN_MSG = "in";
	public static final String OUT_MSG = "out";
	public static final String FAULT_MSG = "fault";
	
	private Throwable 				_error;
	private ExchangeEndpoint 		_sendEndpoint;
	private ExchangeEndpoint 		_recvEndpoint;
	private String 					_exchangeId;
	private ExchangePattern 		_pattern;
	private ExchangeState 			_state;
	private QName 					_service;
	private Map<String, Message> 	_messages = 
		new HashMap<String, Message>();
	private ExchangeContext 		_context = 
		new ExchangeContextImpl();
	
	ExchangeImpl(ExchangePattern pattern) {
		_pattern = pattern;
		_exchangeId = UUID.randomUUID().toString();
		_state = ExchangeState.NEW;
	}

	@Override
	public ExchangeContext getContext() {
		return _context;
	}

	@Override
	public ExchangePattern getPattern() {
		return _pattern;
	}

	@Override
	public QName getService() {
		return _service;
	}

	@Override
	public void setService(QName service) {
		_service = service;
	}
	
	@Override
	public void setError(Throwable error) {
		_error = error;
	}
	
	@Override
	public Throwable getError() {
		return _error;
	}

	@Override
	public String getId() {
		return _exchangeId;
	}

	@Override
	public Message getIn() {
		return getMessage(IN_MSG);
	}

	@Override
	public Message getOut() {
		return getMessage(OUT_MSG);
	}

	@Override
	public void setFault(Message message) {
		setMessage(FAULT_MSG, message);
	}
	
	@Override
	public Message getFault() {
		return getMessage(FAULT_MSG);
	}

	@Override
	public void setIn(Message message) {
		setMessage(IN_MSG, message);
	}

	@Override
	public void setOut(Message message) {
		setMessage(OUT_MSG, message);
	}
	
	
	public ExchangeEndpoint getSendingEndpoint() {
		return _sendEndpoint;
	}
	
	public ExchangeEndpoint getReceivingEndpoint() {
		return _recvEndpoint;
	}
	
	public void setSendingEndpoint(ExchangeEndpoint endpoint) {
		_sendEndpoint = endpoint;
	}

	public void setReceivingEndpoint(ExchangeEndpoint endpoint) {
		_recvEndpoint = endpoint;
	}
	
	public Message getMessage(String name) {
		return _messages.get(name);
	}
	
	public ExchangeState getState() {
		return _state;
	}
	
	public void setState(ExchangeState state) {
		_state = state;
	}

	public void setMessage(String name, Message message) {
		_messages.put(name, message);
	}


}
