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

import javax.xml.namespace.QName;

import org.jboss.esb.cinco.ExchangePattern;
import org.jboss.esb.cinco.components.file.FileComponent;
import org.jboss.esb.cinco.spi.ManagedContext;
import org.jboss.esb.cinco.spi.ServiceContext.Role;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class FileConsumerTest {
	private final QName SERVICE = new QName("TEST_SERVICE");
	
	private ManagedContext _context;
	private FileServiceContext _serviceContext;
	private FileComponent _fileComponent;
	
	@Before
	public void setUp() throws Exception {
		_context = new MockManagedContext();
		_fileComponent = new FileComponent();
		_fileComponent.init(_context);
		_serviceContext = new FileServiceContext();
		_serviceContext.setRole(Role.CONSUMER);
		
		// this is necessary so that there is actually a service available to invoke
		_context.getChannelFactory().createChannel().registerService(SERVICE);
	}
	
	@After
	public void tearDown() throws Exception {
		_fileComponent.destroy();
	}

	@Test
	public void testInOnly() throws Exception {
		/*
		_serviceContext.setPattern(ExchangePattern.IN_ONLY);
		_serviceContext.setTargetPath("/tmp/test");
		_serviceContext.setFilter(".*");
		_fileComponent.deploy(SERVICE, _serviceContext);
		_fileComponent.start(SERVICE);
		
		
		_fileComponent.stop(SERVICE);
		*/
	}
}
