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

import org.jboss.esb.cinco.ServiceDomain;

public class ServiceDomains {
	public static final String ROOT_DOMAIN = "org.jboss.esb.domains.root";

	private static ConcurrentHashMap<String, ServiceDomain> _domains = 
		new ConcurrentHashMap<String, ServiceDomain>();
	
	public synchronized static ServiceDomain getDomain() {
		if (!_domains.contains(ROOT_DOMAIN)) {
			_domains.put(ROOT_DOMAIN, new DomainImpl());
		}
		
		return getDomain(ROOT_DOMAIN);
	}
	
	public static ServiceDomain getDomain(String domainName) {
		return _domains.get(domainName);
	}
	
	public static Set<String> getDomainNames() {
		return _domains.keySet();
	}
}
