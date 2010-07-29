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

package org.jboss.esb.cinco.components.file;

import java.io.File;

import javax.xml.namespace.QName;

import org.jboss.esb.cinco.ExchangePattern;
import org.jboss.esb.cinco.spi.ServiceContext;

public class FileServiceConfig {

	public static final String PATH_KEY = "file.service.path";
	public static final String FILTER_KEY = "file.service.filter";
	
	private ServiceContext _context;
	private QName _serviceName;
	
	public FileServiceConfig(QName serviceName, ServiceContext context) {
		_context = context;
		_serviceName = serviceName;
	}
	
	public File getTargetDir() {
		return new File((String)_context.getConfig().get(PATH_KEY));
	}
	
	public String getFilter() {
		return (String)_context.getConfig().get(FILTER_KEY);
	}
	
	public ExchangePattern getPattern() {
		return _context.getPattern();
	}
	
	public ServiceContext getContext() {
		return _context;
	}
	
	public QName getServiceName() {
		return _serviceName;
	}
}
