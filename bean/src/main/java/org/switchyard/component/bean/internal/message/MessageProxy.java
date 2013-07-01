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
