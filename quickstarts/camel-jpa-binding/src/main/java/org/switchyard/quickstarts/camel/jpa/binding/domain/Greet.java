/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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

    @Column(name = "sender")
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

    @Override
    public String toString() {
        return "Greet [" + _id + ", from " + _sender + ", to " + _receiver + ", created on " + _createdAt.getTime() + "]";
    }

}
