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

package org.jboss.esb.cinco.components.file.test;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.esb.cinco.ExchangePattern;
import org.jboss.esb.cinco.components.file.FileServiceConfig;
import org.jboss.esb.cinco.spi.ServiceContext;

public class FileServiceContext implements ServiceContext {

	private ExchangePattern _pattern;
	private Role _role;
	private QName _serviceRef;
	private Map<String, Object> _config = new HashMap<String, Object>();
	
	public FileServiceContext() {
	}
	
	@Override
	public Map<String, Object> getConfig() {
		return _config;
	}

	@Override
	public ExchangePattern getPattern() {
		return _pattern;
	}

	@Override
	public Role getRole() {
		return _role;
	}

	@Override
	public QName getServiceReference() {
		return _serviceRef;
	}
	
	public void setRole(Role role) {
		_role = role;
	}
	
	public void setPattern(ExchangePattern pattern) {
		_pattern = pattern;
	}
	
	public void setServiceReference(QName serviceRef) {
		_serviceRef = serviceRef;
	}
	
	public void setTargetPath(String path) {
		_config.put(FileServiceConfig.PATH_KEY, path);
	}
	
	public void setFilter(String filter) {
		_config.put(FileServiceConfig.FILTER_KEY, filter);
	}

}
