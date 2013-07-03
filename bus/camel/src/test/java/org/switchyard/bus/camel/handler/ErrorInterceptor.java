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
package org.switchyard.bus.camel.handler;

import java.util.Arrays;
import java.util.List;

import org.switchyard.Exchange;
import org.switchyard.ExchangeInterceptor;
import org.switchyard.HandlerException;

/**
 * Simple handler throwing exception during IN phase.
 */
public class ErrorInterceptor implements ExchangeInterceptor {
    
    private boolean waitForAfter;
    private String target;
    private int count;
    
    public ErrorInterceptor(boolean waitForAfter, String target) {
        this.waitForAfter = waitForAfter;
        this.target = target;
    }

    @Override
    public void before(String target, Exchange exchange) throws HandlerException {
        ++count;
        if (!waitForAfter) {
            throw new HandlerException("Error before on target " + target);
        }
    }

    @Override
    public void after(String target, Exchange exchange) throws HandlerException {
        ++count;
        if (waitForAfter) {
            throw new HandlerException("Error after on target " + target);
        }
    }

    @Override
    public List<String> getTargets() {
        return Arrays.asList(target);
    }

    public int getCount() {
        return count;
    }

}
