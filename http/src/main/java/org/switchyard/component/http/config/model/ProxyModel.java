/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.http.config.model;

import org.switchyard.config.model.Model;

/**
 * A Proxy Configuration Model.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public interface ProxyModel extends Model {

    /**
     * Get host.
     * @return authentication host
     */
    public String getHost();

    /**
     * Set host.
     * @param host the authentication host
     * @return this ProxyModel
     */
    public ProxyModel setHost(String host);

    /**
     * Get authentication port.
     * @return authentication port
     */
    public String getPort();

    /**
     * Set authentication port.
     * @param port the authentication port
     * @return this ProxyModel
     */
    public ProxyModel setPort(String port);

    /**
     * Get user name.
     * @return authentication username
     */
    public String getUser();

    /**
     * Set user name.
     * @param user the user name
     * @return this ProxyModel
     */
    public ProxyModel setUser(String user);

    /**
     * Get user password.
     * @return authentication password
     */
    public String getPassword();

    /**
     * Set user password.
     * @param password the user password
     * @return this ProxyModel
     */
    public ProxyModel setPassword(String password);

}
