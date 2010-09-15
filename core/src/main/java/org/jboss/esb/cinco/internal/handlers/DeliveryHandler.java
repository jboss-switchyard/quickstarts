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

package org.jboss.esb.cinco.internal.handlers;

import org.jboss.esb.cinco.BaseHandler;
import org.jboss.esb.cinco.ExchangeChannel;
import org.jboss.esb.cinco.ExchangeEvent;
import org.jboss.esb.cinco.internal.ExchangeChannelImpl;
import org.jboss.esb.cinco.internal.ExchangeImpl;

public class DeliveryHandler extends BaseHandler {
	

	@Override
	public void handleSend(ExchangeEvent event) {
		ExchangeImpl exchange = (ExchangeImpl)event.getExchange();
		ExchangeChannel receiver = exchange.getReceivingChannel();
		
		if (exchange.getSendingChannel() == null) {
			// first time this has been sent
			exchange.setSendingChannel(event.getChannel());
		}
		else {
			// this is a return path, so flip sender and receiver
			exchange.setReceivingChannel(exchange.getSendingChannel());
			exchange.setSendingChannel(receiver);
		}
		
		((ExchangeChannelImpl)exchange.getReceivingChannel()).deliver(exchange);
	}

}
