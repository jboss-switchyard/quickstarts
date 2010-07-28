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

import org.jboss.esb.cinco.ExchangeChannel;
import org.jboss.esb.cinco.ExchangeChannelFactory;
import org.jboss.esb.cinco.MessageFactory;
import org.jboss.esb.cinco.internal.DefaultChannelFactory;
import org.jboss.esb.cinco.internal.DefaultMessageFactory;
import org.jboss.esb.cinco.internal.DefaultServiceRegistry;

public class Environment {
	
	private DefaultChannelFactory _channelFactory;
	private DefaultServiceRegistry	_registry;
	private MessageFactory _messageFactory;

	public Environment() {
		_registry = new DefaultServiceRegistry();
		_channelFactory = new DefaultChannelFactory(_registry);
		_messageFactory = new DefaultMessageFactory();
	}
	
	public void destroy() {
		for (ExchangeChannel channel : _channelFactory.getChannels()) {
			channel.close();
		}
	}
	
	public MessageFactory getMessageFactory() {
		return _messageFactory;
	}
	
	public ExchangeChannelFactory getExchangeChannelFactory() {
		return _channelFactory;
	}
	 
}
