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

package org.switchyard.internal.event;

import org.switchyard.internal.event.ExchangeState;


public enum ExchangeState {
	IN,
	OUT,
	FAULT,
	UNKNOWN;

	/*
	 * Provide case-insensitive matching for string literals.
	 */
	public static ExchangeState fromString(String state) {
		if (IN.toString().equalsIgnoreCase(state)) {
			return ExchangeState.IN;
		}
		else if (OUT.toString().equalsIgnoreCase(state)) {
			return ExchangeState.OUT;
		}
		else if (FAULT.toString().equalsIgnoreCase(state)) {
			return ExchangeState.FAULT;
		}
		else {
			return ExchangeState.UNKNOWN;
		}
	}
}
