package org.switchyard.components.file;
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
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;

import org.switchyard.components.file.FilePoll;
import org.switchyard.components.file.FileSpool;
import org.switchyard.Context;
import org.switchyard.ExchangePattern;
import org.switchyard.MessageBuilder;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.internal.ServiceDomains;
import org.switchyard.internal.ServiceRegistration;

public class FileComponent {
	
	private static final String SERVICE_TYPE = "file";
	
	private ServiceDomain _domain;
	private Service _service;
	private Context _context;
	private ScheduledExecutorService _scheduler;
	private FileSpool _spooler;
	private Map<QName, Future<?>> _pollers = 
		new HashMap<QName, Future<?>>();
	private Map<QName, FileServiceConfig> _consumedServices = 
		new HashMap<QName, FileServiceConfig>();
	private Map<QName, FileServiceConfig> _providedServices = 
		new HashMap<QName, FileServiceConfig>();

	public FileComponent() {
		_scheduler = Executors.newScheduledThreadPool(2);
		_domain = ServiceDomains.getDomain();
	}

	public void init(Context context) {
		_context = context;
		_spooler = new FileSpool();
		//_channel.getHandlerChain().addLast("file-spooler", _spooler);
	}

	public void destroy() {
	}

	public String getServiceType() {
		return SERVICE_TYPE;
	}

	public void start(QName service) {
		if (_consumedServices.containsKey(service)) {
			createPoller(_consumedServices.get(service));
		}
		else {
			_service = _domain.registerService(service, _spooler);
			FileServiceConfig config = _providedServices.get(service);
			if (config.getExchangePattern().equals(ExchangePattern.IN_OUT)) {
				// we need to set up a polling thread for replies
				createPoller(config);
			}
		}
	}
	
	public void deploy(QName service, Context context, ExchangePattern exchangePattern) {		
		FileServiceConfig config = new FileServiceConfig(service, exchangePattern, context);
		_spooler.addService(config);
		_providedServices.put(service, config);
	}

	public void stop(QName service) {
		if (_providedServices.containsKey(service)) {
			_service.unregister();	
		}
		
		if (_pollers.containsKey(service)) {
			_pollers.get(service).cancel(true);
		}
	}

	public void undeploy(QName service) {
		if (_consumedServices.containsKey(service)) {
			_consumedServices.remove(service);
		}
		else {
			_spooler.removeService(_providedServices.remove(service));
		}
	}
	
	private void createPoller(FileServiceConfig config) {
		FilePoll consumer = new FilePoll(
				config, MessageBuilder.newInstance(), _domain);
		Future<?> scheduledConsumer = _scheduler.scheduleAtFixedRate(
				consumer, 0, 3, TimeUnit.SECONDS);
		_pollers.put(config.getServiceName(), scheduledConsumer);
	}	
}
