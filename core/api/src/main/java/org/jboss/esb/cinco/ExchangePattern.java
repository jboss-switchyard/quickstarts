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


public enum ExchangePattern {

	IN_ONLY ("http://www.w3.org/ns/wsdl/in-only"),
	IN_OUT ("http://www.w3.org/ns/wsdl/in-out");
	
	private String _patternURI;
	
	ExchangePattern(String uri) {
		_patternURI = uri;
	}
	
	public String getURI() {
		return _patternURI;
	}
	
	public static ExchangePattern fromURI(String uri) {
		if (IN_ONLY.getURI().equals(uri)) {
			return IN_ONLY;
		}
		else if (IN_OUT.getURI().equals(uri)) {
			return IN_OUT;
		}
		else {
			throw new IllegalArgumentException("Unrecognized URI: " + uri);
		}
	}
}
