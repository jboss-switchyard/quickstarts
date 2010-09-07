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
import java.io.FileFilter;

import javax.xml.namespace.QName;

import org.jboss.esb.cinco.Exchange;
import org.jboss.esb.cinco.ExchangeChannel;
import org.jboss.esb.cinco.ExchangePattern;
import org.jboss.esb.cinco.Message;
import org.jboss.esb.cinco.MessageFactory;

public class FilePoll implements Runnable {

	private static final String WORK_DIR = "work";
	
	private File 				_pollDir;
	private File 				_workDir;
	private FileServiceConfig	_config;
	private FileFilter 			_pollFilter;
	private ExchangeChannel		_channel;
	private MessageFactory		_messageFactory;
	private QName				_service;
	
	public FilePoll(FileServiceConfig config, ExchangeChannel channel, 
			MessageFactory messageFactory) {
		
		_config = config;
		_channel = channel;
		_service = config.getServiceName();
		_messageFactory = messageFactory;
		
		initDirs();
		_pollFilter = new PollingFilter(config.getFilter());
	}
	
	public void run() {
		for (File f : _pollDir.listFiles(_pollFilter)) {
			if (f.isFile()) {
				File workFile = new File(_workDir, f.getName());
				f.renameTo(workFile);
				send(workFile);
			}
		}
	}
	
	public void send(File file) {
		try {
			String content = FileUtil.readContent(file);
			Message message = _messageFactory.createMessage();
			message.setContent(content);
			
			Exchange exchange = _channel.createExchange(_config.getPattern());
			exchange.setIn(message);
			exchange.setService(_service);
			exchange.getContext().put(Properties.IN_FILE_DIR, _pollDir.getAbsolutePath());
			exchange.getContext().put(Properties.IN_FILE_NAME, file.getName());
			_channel.send(exchange);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	private void initDirs() {
		_pollDir = _config.getTargetDir();
		_workDir = new File(_pollDir, WORK_DIR);
		
		if (_workDir.exists()) {
			if (!_workDir.isDirectory()) {
				throw new RuntimeException(
						"Work directory blocked by file: " + _workDir.getAbsolutePath());
			}
		}
		else if (!_workDir.mkdir()) {
				throw new RuntimeException(
						"Failed to create work directory: " + _workDir.getAbsolutePath());
		}
	}

}
