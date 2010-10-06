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
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.esb.cinco.BaseHandler;
import org.jboss.esb.cinco.Exchange;
import org.jboss.esb.cinco.event.ExchangeInEvent;
import org.jboss.esb.cinco.event.ExchangeOutEvent;

public class FileSpool extends BaseHandler {

	private int _requestCount;
	private Map<QName, FileServiceConfig> _services = 
		new HashMap<QName, FileServiceConfig>();
	
	public FileSpool() {
		super(BaseHandler.Direction.RECEIVE);
	}
	
	public void addService(FileServiceConfig config) {
		_services.put(config.getServiceName(), config);
	}
	
	public void removeService(FileServiceConfig config) {
		_services.remove(config.getServiceName());
	}

	@Override
	public void exchangeIn(ExchangeInEvent event) {
		Exchange exchange = event.getExchange();
		QName service = exchange.getService();
		
		try {
			// Naive approach to file naming : service name + counter
			
			File target = new File(
					_services.get(service).getTargetDir(), 
					service.getLocalPart() + "." + (++_requestCount) + ".request");
			
			// Create the file using content from the message
			FileUtil.writeContent(event.getIn().getContent(String.class), target);
		}
		catch (java.io.IOException ioEx) {
			exchange.setError(ioEx);
		}
		
	}
	
	@Override
	public void exchangeOut(ExchangeOutEvent event) {
		Exchange exchange = event.getExchange();
		
		try {
			// Create the file using content from the message
			FileUtil.writeContent(event.getOut().getContent(String.class), 
					getOutFile(exchange));
		}
		catch (java.io.IOException ioEx) {
			exchange.setError(ioEx);
		}
		
	}
	
	public File getOutFile(Exchange exchange) {
		String dirPath = 
			(String)exchange.getContext().get(Properties.IN_FILE_DIR);
		String fileName =
			(String)exchange.getContext().get(Properties.IN_FILE_NAME);
		
		return new File(new File(dirPath), fileName + ".reply");
	}
}
