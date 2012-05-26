/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.camel.config.model.netty;

/**
 * Configuration binding for tcp gateway.
 * 
 * @author Lukasz Dywicki
 */
public interface CamelNettyTcpBindingModel extends CamelNettyBindingModel {

    /**
     * Mode of endpoint - binary or text based.
     * 
     * @return True if text is chosen.
     */
    Boolean isTextline();

    /**
     * If no codec is specified, you can use this flag to indicate a text line based codec;
     * if not specified or the value is false, then Object Serialization is assumed over TCP.
     * 
     * @param textline True to use text based communication.
     * @return a reference to this binding model
     */
    CamelNettyTcpBindingModel setTextline(Boolean textline);

    /**
     * TCP_NO_DELAY Flag for socket.
     * 
     * @return True if flag should be set.
     */
    Boolean isTcpNoDelay();

    /**
     * Setting to improve TCP protocol performance.
     * 
     * @param tcpNoDelay True if you willing to set TCP_NO_DELAY on socket.
     * @return a reference to this binding model
     */
    CamelNettyTcpBindingModel setTcpNoDelay(Boolean tcpNoDelay);

    /**
     * Should socket be keept open?
     * 
     * @return True to keep socket open.
     */
    Boolean isKeepAlive();

    /**
     * Setting to ensure socket is not closed due to inactivity.
     * 
     * @param keepAlive True to keep socket open.
     * @return a reference to this binding model
     */
    CamelNettyTcpBindingModel setKeepAlive(Boolean keepAlive);

}
