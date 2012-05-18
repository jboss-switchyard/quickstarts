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
package org.switchyard.quickstarts.camel.jpa.binding.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Entity which represents greet.
 */
@Entity
@Table(name = "events")
public class Greet {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long _id;

    @Column(name  = "sender")
    private String _sender;

    @Column(name = "receiver")
    private String _receiver;

    @Column(name = "createdAt")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar _createdAt;

    /**
     * @return the _id
     */
    public Long getId() {
        return _id;
    }

    /**
     * @return the _sender
     */
    public String getSender() {
        return _sender;
    }

    /**
     * @param sender the _sender to set
     */
    public void setSender(String sender) {
        this._sender = sender;
    }

    /**
     * @return the _receiver
     */
    public String getReceiver() {
        return _receiver;
    }

    /**
     * @param receiver the _receiver to set
     */
    public void setReceiver(String receiver) {
        this._receiver = receiver;
    }

    /**
     * @return the createdAt
     */
    public Calendar getCreatedAt() {
        return _createdAt;
    }

    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(Calendar createdAt) {
        this._createdAt = createdAt;
    }

}
