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

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.esb.cinco.EsbException;
import org.jboss.esb.cinco.ServiceDomain;
import org.jboss.esb.cinco.spi.EndpointProvider;
import org.jboss.esb.cinco.spi.ServiceRegistry;

public class ServiceDomains {
	public static final String ROOT_DOMAIN = "org.jboss.esb.domains.root";

	private static ConcurrentHashMap<String, ServiceDomain> _domains = 
		new ConcurrentHashMap<String, ServiceDomain>();
	
	// This is a temporary hack to provide a shared registry and endpoint
	// provider for all domains.  Once we provide initialization properties
	// for a domain, we can remove these and do this a bit more dynamically.
	private static ServiceRegistry _registry = 
		new DefaultServiceRegistry();
	private static EndpointProvider _endpointProvider = 
		new DefaultEndpointProvider();
	
	public synchronized static ServiceDomain getDomain() {
		if (!_domains.containsKey(ROOT_DOMAIN)) {
			try {
				createDomain(ROOT_DOMAIN);
			}
			catch (EsbException esbEx) {
				// obligatory "this should never happen" text
				throw new RuntimeException(esbEx);
			}
		}
		
		return getDomain(ROOT_DOMAIN);
	}
	
	public synchronized static ServiceDomain createDomain(String name) 
	throws EsbException {
		if (_domains.containsKey(name)) {
			throw new EsbException("Domain already exists: " + name);
		}
		
		ServiceDomain domain = new DomainImpl(name, _registry, _endpointProvider);
		_domains.put(name, domain);
		return domain;
	}
	
	public static ServiceDomain getDomain(String domainName) {
		return _domains.get(domainName);
	}
	
	public static Set<String> getDomainNames() {
		return _domains.keySet();
	}
}
