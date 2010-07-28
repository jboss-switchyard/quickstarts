package org.jboss.esb.cinco.components.file;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.xml.namespace.QName;

import org.jboss.esb.cinco.ExchangeChannel;
import org.jboss.esb.cinco.spi.Deployer;
import org.jboss.esb.cinco.spi.Managed;
import org.jboss.esb.cinco.spi.ManagedContext;
import org.jboss.esb.cinco.spi.ServiceContext;

public class FileComponent implements Managed, Deployer {
	
	private static final String SERVICE_TYPE = "file";
	
	private ManagedContext _context;
	private ExchangeChannel	_exchangeChannel;
	private ScheduledExecutorService _pollingPool;
	private Map<QName, FileServiceConfig> _consumedServices = 
		new HashMap<QName, FileServiceConfig>();
	private Map<QName, FileServiceConfig> _providedServices = 
		new HashMap<QName, FileServiceConfig>();

	public FileComponent() {
		_pollingPool = Executors.newScheduledThreadPool(2);
	}

	@Override
	public void init(ManagedContext context) {
		_context = context;
		_exchangeChannel = _context.getChannelFactory().createChannel();
	}

	@Override
	public void destroy() {
		_exchangeChannel.close();
	}


	@Override
	public String getServiceType() {
		return SERVICE_TYPE;
	}

	@Override
	public void start(QName service) {
		if (_consumedServices.containsKey(service)) {
			
		}
		else {
			
		}
	}
	
	@Override
	public void deploy(QName service, ServiceContext context) {
		
		FileServiceConfig config = new FileServiceConfig(context);
		
		if (context.getRole().equals(ServiceContext.Role.CONSUMER)) {
			_consumedServices.put(service, config);
		}
		else {
			_providedServices.put(service, config);
		}
	}

	@Override
	public void stop(QName service) {
		if (_consumedServices.containsKey(service)) {
		}
		else {
		}
	}

	@Override
	public void undeploy(QName service) {
		
		// blech - this sucks
		_consumedServices.remove(service);
		_providedServices.remove(service);
	}
	
}
