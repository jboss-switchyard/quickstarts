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
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;

import org.jboss.esb.cinco.ExchangeChannel;
import org.jboss.esb.cinco.ExchangePattern;
import org.jboss.esb.cinco.spi.Deployer;
import org.jboss.esb.cinco.spi.Managed;
import org.jboss.esb.cinco.spi.ManagedContext;
import org.jboss.esb.cinco.spi.ServiceContext;

public class FileComponent implements Managed, Deployer {
	
	private static final String SERVICE_TYPE = "file";
	
	private ManagedContext _context;
	private ExchangeChannel	_channel;
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
	}

	@Override
	public void init(ManagedContext context) {
		_context = context;
		_channel = _context.getChannelFactory().createChannel();
		_spooler = new FileSpool();
		_channel.getHandlerChain().addLast("file-spooler", _spooler);
	}

	@Override
	public void destroy() {
		_channel.close();
	}


	@Override
	public String getServiceType() {
		return SERVICE_TYPE;
	}

	@Override
	public void start(QName service) {
		if (_consumedServices.containsKey(service)) {
			createPoller(_consumedServices.get(service));
		}
		else {
			_channel.registerService(service);
			FileServiceConfig config = _providedServices.get(service);
			if (config.getPattern().equals(ExchangePattern.IN_OUT)) {
				// we need to set up a polling thread for replies
				createPoller(config);
			}
		}
	}
	
	@Override
	public void deploy(QName service, ServiceContext context) {
		
		FileServiceConfig config = new FileServiceConfig(service, context);
		// This would be a great time to validate the config
		
		if (context.getRole().equals(ServiceContext.Role.CONSUMER)) {
			_consumedServices.put(service, config);
		}
		else {
			_providedServices.put(service, config);
			_spooler.addService(config);
		}
	}

	@Override
	public void stop(QName service) {
		if (_providedServices.containsKey(service)) {
			_channel.unregisterService(service);
		}
		
		if (_pollers.containsKey(service)) {
			_pollers.get(service).cancel(true);
		}
	}

	@Override
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
				config, _channel, _context.getMessageFactory());
		Future<?> scheduledConsumer = _scheduler.scheduleAtFixedRate(
				consumer, 0, 3, TimeUnit.SECONDS);
		_pollers.put(config.getServiceName(), scheduledConsumer);
	}
	
}
