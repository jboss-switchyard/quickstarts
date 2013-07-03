/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */

package org.switchyard;

import java.util.List;

/**
 * ExchangeInterceptors allow code to be injected around specific interactions
 * in the core exchange bus via a target definition.  Valid targets are documented
 * as constant fields in this interface.
 */
public interface ExchangeInterceptor {
    
    /**
     * Intercepts an exchange before a message is passed into the processing pipeline
     * and after all processing steps have completed.
     */
    String CONSUMER = "Consumer";
    
    /**
     * Intercepts an exchange immediately before the provider is invoked and again
     * immediately after the provider returns.
     */
    String PROVIDER = "Provider";
    
    /**
     * Invoked immediately before the specified target scope in a message exchange.
     * @param target interception point
     * @param exchange message exchange
     * @throws HandlerException in order to fail a message exchange
     */
    void before(String target, Exchange exchange) throws HandlerException;
    
    /**
     * Invoked immediately after the specified target scope in a message exchange.
     * @param target interception point
     * @param exchange message exchange
     * @throws HandlerException in order to fail a message exchange
     */
    void after(String target, Exchange exchange) throws HandlerException;
    
    /**
     * The list of targets, or interception points, where this interceptor should be invoked.
     * @return list of targets where this interceptor applies.
     */
    List<String> getTargets();
}
