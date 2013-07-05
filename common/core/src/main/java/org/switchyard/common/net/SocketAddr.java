/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package org.switchyard.common.net;

import java.io.Serializable;

/**
 * SocketAddr is a wrapper for IP address or host name + port number combination.
 * No host name resolution will be performed by this class.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class SocketAddr implements Serializable {

    private static final long serialVersionUID = 628840973822138641L;

    /**
     * The default host this socket will be associated with.
     */
    public static final String DEFAULT_HOST = "127.0.0.1";

    /**
     * The default port this socket will be associated with.
     */
    public static final int DEFAULT_PORT = 8080;

    private String _host = DEFAULT_HOST;
    private int _port = DEFAULT_PORT;

    /**
     * Default Constructor.
     */
    public SocketAddr() {
    }

    /**
     * Construct the SocketAddr from a string.
     *
     * The string can be in the form "hostName/ipAddress:portNumber", with the "hostName/ipAddress:" or ":portNumber" part being optional.
     *
     * @param socket a port name String.
     */
    public SocketAddr(final String socket) {
        if (socket != null) {
            String[] socketTokens = socket.split(":");
            if (socketTokens.length > 0 && socketTokens[0].length() > 0) {
                _host = socketTokens[0];
            }
            if (socketTokens.length > 1 && socketTokens[1].length() > 0) {
                _port = Integer.valueOf(socketTokens[1]);
            }
        }
    }

    /**
     * Construct the SocketAddr from host/ip and port values.
     *
     * @param host a host name or ip address.
     * @param port a port number.
     */
    public SocketAddr(String host, int port) {
        _host = host;
        _port = port;
    }

    /**
     * Set the host name or ip address value.
     * 
     * @param host the host value to set
     */
    public void setHost(String host) {
        this._host = host;
    }

    /**
     * Get the host or ip address value.
     * 
     * @return the host
     */
    public String getHost() {
        return _host;
    }

    /**
     * Set the port value.
     * 
     * @param port the port to set
     */
    public void setPort(int port) {
        this._port = port;
    }

    /**
     * Get the port value.
     * 
     * @return the port
     */
    public int getPort() {
        return _port;
    }

    /**
     * Test this SocketAddr for equality with another Object.
     *
     * @param objectToTest the Object to test with this PortName.
     * @return true if equal else false.
     */
    public final boolean equals(Object objectToTest) {
        if ((objectToTest == null) || (!(objectToTest instanceof SocketAddr))) {
            return false;
        }

        SocketAddr socket = (SocketAddr) objectToTest;

        return (this._host.equals(socket._host)) && (this._port == socket._port);
    }

    /**
     * Generate the hashcode for this SocketAddr.
     *
     * @return The hashcode.
     */
    public final int hashCode() {
        return _host.hashCode() ^ Integer.valueOf(_port).hashCode();
    }

    /**
     * Retruns a String representation of SocketAddr in the form "host:port".
     *
     * @return A SocketAddr string.
     */
    public String toString() {
        return _host + ":" + _port;
    }
}
