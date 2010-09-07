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

package org.jboss.esb.cinco.framework.internal;

import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.esb.cinco.ExchangePattern;
import org.jboss.esb.cinco.spi.ServiceContext;

public class ServiceContextImpl implements ServiceContext {

	private Role _role;
	private QName _serviceRef;
	private ExchangePattern _pattern;
	private Map<String,Object> _config;
	
	public ServiceContextImpl(Role role, ExchangePattern pattern, 
			Map<String,Object> config, QName serviceRef) {
		_role = role;
		_config = config;
		_pattern = pattern;
		_serviceRef = serviceRef;
	}
	
	@Override
	public Map<String, Object> getConfig() {
		return _config;
	}

	@Override
	public Role getRole() {
		return _role;
	}

	@Override
	public ExchangePattern getPattern() {
		return _pattern;
	}

	@Override
	public QName getServiceReference() {
		return _serviceRef;
	}

}
