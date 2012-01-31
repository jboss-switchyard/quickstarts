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
package org.switchyard.component.bpm.task.service;

import java.util.List;
import java.util.Locale;

/**
 * A base class for task clients.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class BaseTaskClient implements TaskClient {

    private static final Locale EN_UK = new Locale("en", "UK");

    private String _host = "127.0.0.1";
    private int _port = 9123;
    private boolean _connected = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHost() {
        return _host;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskClient setHost(String host) {
        _host = host;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPort() {
        return _port;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskClient setPort(int port) {
        _port = port;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnected() {
        return _connected;
    }

    /**
     * Sets the connected status.
     * @param connected if connected
     * @return this instance
     */
    protected TaskClient setConnected(boolean connected) {
        _connected = connected;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Task> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds) {
        return getTasksAssignedAsPotentialOwner(userId, groupIds, EN_UK);
    }

}
