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

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.switchyard.common.io.pull.PropertiesPuller;
import org.switchyard.common.lang.Strings;
import org.switchyard.exception.SwitchYardException;

/**
 * A base class for task servers.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class BaseTaskServer implements TaskServer {

    private static final Logger LOGGER = Logger.getLogger(BaseTaskServer.class);

    private String _host = "127.0.0.1";
    private int _port = 9123;
    private boolean _started = false;
    private String _usersPath = "/users.properties";
    private String _groupsPath = "/roles.properties";

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
    public TaskServer setHost(String host) {
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
    public TaskServer setPort(int port) {
        _port = port;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStarted() {
        return _started;
    }

    /**
     * Sets the started status.
     * @param started if started
     * @return this instance
     */
    protected TaskServer setStarted(boolean started) {
        _started = started;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsersPath() {
        return _usersPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskServer setUsersPath(String usersPath) {
        _usersPath = usersPath;
        return this;
    }

    /**
     * Gets the users properties.
     * @return the users properties
     */
    protected Properties getUsersProperties() {
        try {
            return new PropertiesPuller().pull(getUsersPath(), BaseTaskServer.class);
        } catch (IOException ioe) {
            throw new SwitchYardException(ioe);
        }
    }

    /**
     * Gets the users set.
     * @param initialUsers users to add in the set first.
     * @return the users set
     */
    protected Set<String> getUsersSet(String... initialUsers) {
        Set<String> usersSet = new LinkedHashSet<String>();
        for (String initialUser : initialUsers) {
            String user = Strings.trimToNull(initialUser);
            if (user != null) {
                usersSet.add(user);
            }
        }
        Properties usersProperties = getUsersProperties();
        if (usersProperties != null) {
            for (Object key : usersProperties.keySet()) {
                String user = Strings.trimToNull((String)key);
                if (user != null) {
                    usersSet.add(user);
                }
            }
        }
        return usersSet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getGroupsPath() {
        return _groupsPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskServer setGroupsPath(String groupsPath) {
        _groupsPath = groupsPath;
        return this;
    }

    /**
     * Gets the groups properties.
     * @return the groups properties
     */
    protected Properties getGroupsProperties() {
        try {
            return new PropertiesPuller().pull(getGroupsPath(), BaseTaskServer.class);
        } catch (IOException ioe) {
            throw new SwitchYardException(ioe);
        }
    }

    /**
     * Gets the groups set.
     * @param initialGroups groups to add in the set first.
     * @return the groups set
     */
    protected Set<String> getGroupsSet(String... initialGroups) {
        Set<String> groupsSet = new LinkedHashSet<String>();
        for (String initialGroup : initialGroups) {
            String group = Strings.trimToNull(initialGroup);
            if (group != null) {
                groupsSet.add(group);
            }
        }
        Properties groupsProperties = getGroupsProperties();
        if (groupsProperties != null) {
            for (Object key : groupsProperties.keySet()) {
                String value = groupsProperties.getProperty((String)key);
                StringTokenizer st = new StringTokenizer(value, ",");
                while (st.hasMoreTokens()) {
                    String group = Strings.trimToNull(st.nextToken());
                    if (group != null) {
                        groupsSet.add(group);
                    }
                }
            }
        }
        return groupsSet;
    }

    /**
     * Waits for readiness of a port.
     * @param wantFree whether you're waiting for the port to be free (true) or busy (false)
     * @return if ready
     */
    protected boolean waitForPort(final boolean wantFree) {
        int attempts = 0;
        boolean ready = false;
        while (!ready && attempts < 11) {
            attempts++;
            Socket socket = null;
            try {
                socket = new Socket(getHost(), getPort());
                final boolean connected = socket.isConnected();
                if ((wantFree && connected) || (!wantFree && !connected)) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        LOGGER.trace(ie.getMessage());
                    }
                } else {
                    ready = true;
                }
            } catch (IOException ioe) {
                if (wantFree) {
                    ready = true;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        LOGGER.trace(ie.getMessage());
                    }
                }
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException ioe) {
                        LOGGER.warn(ioe.getMessage());
                    } finally {
                        socket = null;
                    }
                }
            }
        }
        return ready;
    }

}
