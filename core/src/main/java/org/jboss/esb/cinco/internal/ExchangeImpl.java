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

import javax.xml.namespace.QName;

import org.jboss.esb.cinco.Exchange;
import org.jboss.esb.cinco.ExchangeContext;
import org.jboss.esb.cinco.ExchangeState;
import org.jboss.esb.cinco.Message;

public class ExchangeImpl implements Exchange {
	
	private Throwable _error;
	private String _patternURI;
	private ExchangeState _state;
	private QName _service;
	private Map<String, Message> _messages = 
		new HashMap<String, Message>();
	private ExchangeContext _context = new ExchangeContextImpl();
	
	ExchangeImpl(String patternURI) {
		_patternURI = patternURI;
	}

	@Override
	public ExchangeContext getContext() {
		return _context;
	}

	@Override
	public Message getMessage(String name) {
		return _messages.get(name);
	}

	@Override
	public String getPattern() {
		return _patternURI;
	}

	@Override
	public QName getService() {
		return _service;
	}

	@Override
	public void setMessage(String name, Message message) {
		_messages.put(name, message);
		setState(ExchangeState.fromString(name));
	}

	@Override
	public void setService(QName service) {
		_service = service;
	}

	@Override
	public ExchangeState getState() {
		return _state;
	}
	
	void setState(ExchangeState state) {
		_state = state;
	}

	@Override
	public void done() {
		setState(ExchangeState.DONE);
		
	}

	@Override
	public void setError(Throwable error) {
		setState(ExchangeState.ERROR);
		_error = error;
	}
	

	@Override
	public Throwable getError() {
		return _error;
	}
	
}
