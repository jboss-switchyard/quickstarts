/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.bpm.task;

/**
 * Represents a task server.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public interface TaskServer {

    /**
     * Gets the host.
     * @return the host
     */
    public String getHost();

    /**
     * Sets the host.
     * @param host the host
     * @return this instance
     */
    public TaskServer setHost(String host);

    /**
     * Gets the port.
     * @return the port
     */
    public int getPort();

    /**
     * Sets the port.
     * @param port the port
     * @return this instance
     */
    public TaskServer setPort(int port);

    /**
     * Gets the started status.
     * @return true if started
     */
    public boolean isStarted();

    /**
     * Gets the users path.
     * @return the users path
     */
    public String getUsersPath();

    /**
     * Sets the users path.
     * @param usersPath the users path
     * @return this instance
     */
    public TaskServer setUsersPath(String usersPath);

    /**
     * Gets the groups path.
     * @return the groups path
     */
    public String getGroupsPath();

    /**
     * Sets the groups path.
     * @param groupsPath the groups path
     * @return this instance
     */
    public TaskServer setGroupsPath(String groupsPath);

    /**
     * Starts the TaskServer.
     */
    public void start();

    /**
     * Stops the TaskServer.
     */
    public void stop();

    /**
     * Bootstrap for running command line.
     */
    public static final class Main {

        /**
         * CLI.
         * @param args none required
         */
        public static final void main(String... args) {
            final TaskServer server = TaskService.instance().newTaskServer();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    server.stop();
                }
            });
            server.start();
        }
    }

}
