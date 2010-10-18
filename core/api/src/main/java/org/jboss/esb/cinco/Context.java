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

package org.jboss.esb.cinco;

import java.util.Map;


public interface Context {
	/* This is a list of commonly used context keys.  Need to think long and
	 * hard about whether this is the right place for a key mapping.  Might be
	 * better to externalize this in a const class and/or provide
	 * subclasses of context for each scope and have specific methods to get/set
	 * these properties.
	 */
	public static final String CORRELATION_ID = 
		"org.jboss.esb.cinco.context.correlation.id";
	public static final String MESSAGE_ID = 
		"org.jboss.esb.cinco.context.message.id";
	public static final String MESSAGE_NAME = 
		"org.jboss.esb.cinco.context.message.name";
	
	Object getProperty(String name);
	Map<String, Object> getProperties();
	boolean hasProperty(String name);
	Object removeProperty(String name);
	void setProperty(String name, Object val);
}
