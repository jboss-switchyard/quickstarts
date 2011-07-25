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
package org.switchyard.component.hornetq.config.model;

import org.switchyard.config.model.Model;

/**
 * A HornetQDiscoveryGroupConfigModel represents a discovery group configuration in HornetQ which
 * enables clients to connect to server without having prior knowledage about what servers exist.
 * 
 * Discovery is done by using UDP and hence requires that UDP is enabled on your network.
 * 
 * @author Daniel Bevenius
 *
 */
public interface HornetQDiscoveryGroupConfigModel extends Model {
    
    /**
     * The name of this configuration element.
     */
    String DISCOVERY_GROUP = "discoveryGroup";
    
    /**
     * The configuration property name for the local bind address.
     */
    String LOCAL_BIND_ADDRESS = "localBindAddress";
    
    /**
     * The configuration property name for the group address.
     */
    String GROUP_ADDRESS = "groupAddress";
    
    /**
     * The configuration property name for the group port.
     */
    String GROUP_PORT = "groupPort";
    
    /**
     * The configuration property name for the refresh timeout.
     */
    String REFRESH_TIMEOUT = "refreshTimeout";
    
    
    /**
     * The configuration property name for the initial wait timeout.
     */
    String INITIAL_WAIT_TIMEOUT = "initialWaitTimeout";
    
    /**
     * Sets the local bind address.
     * This is the local address that the datagram socket is bound to.
     * 
     * @param address the local bind address to use.
     * @return HornetQDiscoveryGroupConfigModel to support method chaining.
     */
    HornetQDiscoveryGroupConfigModel setLocalBindAddress(String address);
    
    /**
     * Gets the configured local bind address.
     * 
     * @return String the configured local bind address, or null if no was configured.
     */
    String getLocalBindAddress();
    
    /**
     * Sets the group address which is the multicast address to which data will be broadcast.
     * 
     * @param address the group address to use.
     * @return HornetQDiscoveryGroupConfigModel to support method chaining.
     */
    HornetQDiscoveryGroupConfigModel setGroupAddress(String address);
    
    /**
     * Gets the configured group address.
     * 
     * @return String the configured group address.
     */
    String getGroupAddress();
    
    /**
     * Sets the group port which is the UDP port number used for broadcasting.
     * 
     * @param port the port to use for UDP broadcasting.
     * @return HornetQDiscoveryGroupConfigModel to support method chaining.
     */
    HornetQDiscoveryGroupConfigModel setGroupPort(Integer port);
    
    /**
     * Gets the group port number.
     * 
     * @return Integer the group port number.
     */
    Integer getGroupPort();

    /**
     * Sets the refresh time out which is the period this discovery group waits after receiving the last 
     * broadcast from a particular server before removing that servers connector pair from its list.
     * 
     * @param timeout the timeout in ms.
     * @return HornetQDiscoveryGroupConfigModel to support method chaining.
     */
    HornetQDiscoveryGroupConfigModel setRefreshTimeout(Long timeout);
    
    /**
     * Gets the refresh timeout.
     * 
     * @return Long the referesh timeout, or null if no timeout was configured.
     */
    Long getRefreshTimeout();
    
    /**
     * Sets the initial wait time to give the underlying session factory time to receive broadcasts
     * from all the nodes in the cluster.
     * On first usage the session factory will wait for the amount of time configured by this method
     * before creating the first session. 
     * 
     * @param timeout the timeout in ms.
     * @return HornetQDiscoveryGroupConfigModel to support method chaining.
     */
    HornetQDiscoveryGroupConfigModel setInitialWaitTimeout(Long timeout);
    
    /**
     * Gets the configured initail wait timeout.
     * 
     * @return Long the configured initial wait timeout, or null if not configured.
     */
    Long getInitialWaitTimeout();
    
}
