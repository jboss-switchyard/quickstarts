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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.esb.cinco.ExchangeChannel;
import org.jboss.esb.cinco.spi.ServiceRegistry;

public class DefaultServiceRegistry implements ServiceRegistry {
	
	private Map<QName, List<ExchangeChannel>> _services = 
		new HashMap<QName, List<ExchangeChannel>>();

	@Override
	public synchronized List<ExchangeChannel> getChannels(QName serviceName) {
		List<ExchangeChannel> channels = _services.get(serviceName);
		if (channels == null) {
			channels = Collections.emptyList();
		}
		return channels;
	}

	@Override
	public synchronized void registerService(QName serviceName, ExchangeChannel endpoint) {
		List<ExchangeChannel> channels = _services.get(serviceName);
		if (channels == null) {
			channels = new ArrayList<ExchangeChannel>();
			_services.put(serviceName, channels);
		}
		channels.add(endpoint);
	}

	@Override
	public synchronized void unregisterService(QName serviceName, ExchangeChannel endpoint) {
		List<ExchangeChannel> channels = _services.get(serviceName);
		if (channels != null) {
			channels.remove(endpoint);
		}
	}
}
