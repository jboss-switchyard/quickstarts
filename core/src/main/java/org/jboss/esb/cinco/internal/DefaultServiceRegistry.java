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

import org.jboss.esb.cinco.spi.ExchangeEndpoint;
import org.jboss.esb.cinco.spi.ServiceRegistry;

public class DefaultServiceRegistry implements ServiceRegistry {
	
	private Map<QName, List<ExchangeEndpoint>> _serviceEndpoints = 
		new HashMap<QName, List<ExchangeEndpoint>>();

	@Override
	public synchronized List<ExchangeEndpoint> getEndpoints(QName serviceName) {
		List<ExchangeEndpoint> endpoints = _serviceEndpoints.get(serviceName);
		if (endpoints == null) {
			endpoints = Collections.emptyList();
		}
		return endpoints;
	}

	@Override
	public synchronized void registerService(QName serviceName, ExchangeEndpoint endpoint) {
		List<ExchangeEndpoint> endpoints = _serviceEndpoints.get(serviceName);
		if (endpoints == null) {
			endpoints = new ArrayList<ExchangeEndpoint>();
			_serviceEndpoints.put(serviceName, endpoints);
		}
		endpoints.add(endpoint);
	}

	@Override
	public synchronized void unregisterService(QName serviceName, ExchangeEndpoint endpoint) {
		List<ExchangeEndpoint> endpoints = _serviceEndpoints.get(serviceName);
		if (endpoints != null) {
			endpoints.remove(endpoint);
		}
	}
}
