package org.jboss.esb.cinco.components.file.test;

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

import java.io.File;

import javax.xml.namespace.QName;

import org.jboss.esb.cinco.Exchange;
import org.jboss.esb.cinco.ExchangeChannel;
import org.jboss.esb.cinco.ExchangePattern;
import org.jboss.esb.cinco.Message;
import org.jboss.esb.cinco.components.file.FileComponent;
import org.jboss.esb.cinco.spi.ManagedContext;
import org.jboss.esb.cinco.spi.ServiceContext.Role;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class FileProviderTest {
	private final QName IN_ONLY_SERVICE = new QName("IN_ONLY_SERVICE");
	private final QName IN_OUT_SERVICE = new QName("IN_OUT_SERVICE");
	
	private File _testRoot = new File("target/test/FileProviderTest");
	private File _requestDir = new File(_testRoot, "request");
	private File _replyDir = new File(_testRoot, "reply");
	
	private ManagedContext _context;
	private FileServiceContext _serviceContext;
	private FileComponent _fileComponent;
	
	@Before
	public void setUp() throws Exception {
		// clean up from last run
		Util.delete(_testRoot);
		
		_context = new MockManagedContext();
		_fileComponent = new FileComponent();
		_fileComponent.init(_context);
		_serviceContext = new FileServiceContext();
		_serviceContext.setRole(Role.PROVIDER);
		
		_requestDir.mkdirs();
		_replyDir.mkdirs();
	}
	
	@After
	public void tearDown() throws Exception {
		_fileComponent.destroy();
	}

	@Test
	public void testInOnly() throws Exception {
		
		_serviceContext.setPattern(ExchangePattern.IN_ONLY);
		_serviceContext.setTargetPath(_requestDir.getAbsolutePath());
		_fileComponent.deploy(IN_ONLY_SERVICE, _serviceContext);
		_fileComponent.start(IN_ONLY_SERVICE);
		
		invokeService(IN_ONLY_SERVICE, ExchangePattern.IN_ONLY, "InOnly Test");
		
		// wait for the file spooler to pick up the message and write the file
		Thread.sleep(500);
		
		_fileComponent.stop(IN_ONLY_SERVICE);
		
		Assert.assertTrue(_requestDir.listFiles().length == 1);
	}
	
	private void invokeService(QName service, ExchangePattern pattern, String content) {
		ExchangeChannel channel = _context.getChannelFactory().createChannel();
		Exchange exchange = channel.createExchange(pattern);
		Message message = _context.getMessageFactory().createMessage();
		message.setContent(content);
		exchange.setIn(message);
		exchange.setService(service);
		channel.send(exchange);
	}
}
