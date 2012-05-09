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
package org.switchyard.component.camel.config.model.ftp;

import org.switchyard.component.camel.config.model.remote.CamelRemoteFileBindingModel;

/**
 * Ftp endpoint binding.
 * 
 * @author Lukasz Dywicki
 */
public interface CamelFtpBindingModel extends CamelRemoteFileBindingModel {

    /**
     * Should connection be used in passive mode.
     * 
     * @return Passive mode.
     */
    Boolean isPassiveMode();

    /**
     * Sets passive mode.
     * 
     * @param passive Passive mode.
     * @return a reference to this binding model
     */
    CamelRemoteFileBindingModel setPassiveMode(boolean passive);

    /**
     * Gets timeout parameter.
     * 
     * @return Timeout (in milliseconds).
     */
    Integer getTimeout();

    /**
     * Sets timeout.
     * 
     * @param timeout Timeout.
     * @return a reference to this binding model
     */
    CamelRemoteFileBindingModel setTimeout(int timeout);

    /**
     * Get socket timeout.
     * 
     * @return Socket timeout (in milliseconds).
     */
    Integer getSoTimeout();

    /**
     * Sets socket timeout.
     * 
     * @param timeout Timeout (in milliseconds).
     * @return a reference to this binding model
     */
    CamelRemoteFileBindingModel setSoTimeout(int timeout);

    /**
     * Gets site command.
     * 
     * @return Site command.
     */
    String getSiteCommand();

    /**
     * Sets site command.
     * 
     * @param command Site command.
     * @return a reference to this binding model
     */
    CamelRemoteFileBindingModel setSiteCommand(String command);

}
