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

import org.jboss.esb.cinco.Context;
import org.jboss.esb.cinco.components.file.FileComponent;
import org.jboss.esb.cinco.internal.BaseContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class FileConsumerTest {
	private final QName IN_ONLY_SERVICE = new QName("consumer-in-only");
	private final QName IN_OUT_SERVICE = new QName("consumer-in-out");
	
	private File _testMsg = new File("target/test-classes/message.txt");
	private File _testRoot = new File("target/test/FileConsumerTest");
	
	private Context _context;
	private FileServiceContext _serviceContext;
	private FileComponent _fileComponent;
	
	@Before
	public void setUp() throws Exception {
		// clean up from last run
		Util.delete(_testRoot);
		
		_testRoot.mkdirs();
		
		_context = new BaseContext();
		_fileComponent = new FileComponent();
		_fileComponent.init(_context);
		_serviceContext = new FileServiceContext();
//		_serviceContext.setRole(Role.CONSUMER);
		
		Util.copyFile(_testMsg, new File(_testRoot, "test.request"));
	}
	
	@After
	public void tearDown() throws Exception {
		_fileComponent.destroy();
	}

	@Test
	public void testInOnly() throws Exception {
		/*
		_serviceContext.setPattern(ExchangePattern.IN_ONLY);
		_serviceContext.setTargetPath(_testRoot.getAbsolutePath());
		_serviceContext.setFilter(".*request");
		_fileComponent.deploy(IN_ONLY_SERVICE, _serviceContext, ExchangePattern.IN_ONLY);
		_fileComponent.start(IN_ONLY_SERVICE);
		
		// wait for the file poller to pick up any test files
		Thread.sleep(500);
		
		_fileComponent.stop(IN_ONLY_SERVICE);
		*/
	}
	

	@Test
	public void testInOut() throws Exception {
		/*
		MessageBuilder mb = MessageBuilder.newInstance();
		Message replyMsg = mb.buildMessage();
		replyMsg.setContent("Reply Message");
		//provider.setReply(replyMsg);
		//provider.provideService(IN_OUT_SERVICE);
		
		_serviceContext.setPattern(ExchangePattern.IN_OUT);
		_serviceContext.setTargetPath(_testRoot.getAbsolutePath());
		_serviceContext.setFilter(".*request");
		_fileComponent.deploy(IN_OUT_SERVICE, _serviceContext, ExchangePattern.IN_OUT);
		_fileComponent.start(IN_OUT_SERVICE);
	
		// wait for the file poller to pick up any test files
		Thread.sleep(500);
		
		_fileComponent.stop(IN_OUT_SERVICE);
		
		//Assert.assertTrue(provider.getReceiveCount() == 1);

		File[] replies = _testRoot.listFiles(Util.createFilter(".*reply"));
		//Assert.assertTrue(replies.length == 1);
		 */
	}
}
