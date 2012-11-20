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
package org.switchyard.quickstarts.camel.sql.binding;

/**
 * Simple greeting pojo.
 */
public class Greeting {

    private String _name;
    private String _sender;

    /**
     * Creates greeting.
     * 
     * @param name Person to receive greet.
     * @param sender Greeting sender.
     */
    public Greeting(String name, String sender) {
        this._name = name;
        this._sender = sender;
    }

    /**
     * Gets receiver name.
     * @return Receiver name.
     */
    public String getName() {
        return _name;
    }

    /**
     * Sets receiver name.
     * @param name Name of the receiver.
     */
    public void setName(String name) {
        this._name = name;
    }

    /**
     * Gets sender name.
     * @return Sender name.
     */
    public String getSender() {
        return _sender;
    }

    public void setSender(String sender) {
        this._sender = sender;
    }

}
