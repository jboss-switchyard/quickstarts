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
package org.switchyard.component.bean.internal.message;

import java.util.Map;

import javax.activation.DataSource;
import javax.enterprise.context.ApplicationScoped;

import org.switchyard.Context;
import org.switchyard.Message;

/**
 * SwitchYard Message proxy.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
@ApplicationScoped
public class MessageProxy implements Message {

    private static final ThreadLocal<Message> MESSAGE = new ThreadLocal<Message>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Context getContext() {
        return getMessage().getContext();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message setContent(Object content) {
        return getMessage().setContent(content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getContent() {
        return getMessage().getContent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getContent(Class<T> type) {
        return getMessage().getContent(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message addAttachment(String name, DataSource attachment) {
        return getMessage().addAttachment(name, attachment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSource getAttachment(String name) {
        return getMessage().getAttachment(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAttachment(String name) {
        getMessage().removeAttachment(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, DataSource> getAttachmentMap() {
        return getMessage().getAttachmentMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message copy() {
        return getMessage().copy();
    }

    /**
     * Gets the {@link Message} for the current thread.
     * @return the message
     */
    private static Message getMessage() {
        Message message = MESSAGE.get();
        if (message == null) {
            throw new IllegalStateException("Illegal call to get the SwitchYard Message; must be called within the execution of an ExchangeHandler chain.");
        }
        return message;
    }

    /**
     * Sets the {@link Message} for the current thread.
     * @param message the message
     */
    public static void setMessage(Message message) {
        if (message != null) {
            MESSAGE.set(message);
        } else {
            MESSAGE.remove();
        }
    }

}
