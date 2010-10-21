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

package org.switchyard.components.file;

import java.io.File;
import java.io.FileFilter;

import javax.xml.namespace.QName;

import org.switchyard.components.file.FileUtil;
import org.switchyard.components.file.PollingFilter;
import org.switchyard.components.file.Properties;
import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.MessageBuilder;
import org.switchyard.ServiceDomain;

public class FilePoll implements Runnable {

	private static final String WORK_DIR = "work";
	
	private File 				_pollDir;
	private File 				_workDir;
	private FileServiceConfig	_config;
	private FileFilter 			_pollFilter;
	private MessageBuilder		_messageBuilder;
	private QName				_service;
	private ServiceDomain		_domain;
	
	public FilePoll(FileServiceConfig config, MessageBuilder messageFactory, ServiceDomain domain) {
		_domain = domain;
		_config = config;
		_service = config.getServiceName();
		_messageBuilder = messageFactory;
		
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
			Message message = _messageBuilder.buildMessage();
			message.setContent(content);
			
			Exchange exchange = _domain.createExchange(_service, _config.getExchangePattern());
			exchange.getContext().setProperty(Properties.IN_FILE_DIR, _pollDir.getAbsolutePath());
			exchange.getContext().setProperty(Properties.IN_FILE_NAME, file.getName());
			exchange.sendIn(message);
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
