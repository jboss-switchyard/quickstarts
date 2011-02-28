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

package org.switchyard.internal;

import java.util.HashMap;
import java.util.Map;

import javax.activation.DataSource;

import org.switchyard.Context;
import org.switchyard.Message;

/**
 * Default message.
 */
public class DefaultMessage implements Message {
    
    private Context _context;
    private Object _content;
    private Map<String, DataSource> _attachments = 
        new HashMap<String, DataSource>();
    

    /**
     * Create a new instance of DefaultMessage with an empty context.
     */
    public DefaultMessage() {
        _context = new DefaultContext();
    }
    
    /**
     * Create a new instance of DefaultMessage with the specified context.
     * @param context context instance to use with this message
     */
    public DefaultMessage(Context context) {
        _context = context;
    }
    
    @Override
    public DefaultMessage addAttachment(final String name, final DataSource attachment) {
        _attachments.put(name, attachment);
        return this;
    }

    @Override
    public DataSource getAttachment(final String name) {
        return _attachments.get(name);
    }

    @Override
    public DataSource removeAttachment(final String name) {
        return _attachments.remove(name);
    }

    @Override
    public Map<String, DataSource> getAttachmentMap() {
        return new HashMap<String, DataSource>(_attachments);
    }

    @Override
    public Object getContent() {
        return _content;
    }

    @Override
    public <T> T getContent(final Class<T> type) {
        return type.cast(_content);
    }

    @Override
    public DefaultMessage setContent(final Object content) {
        _content = content;
        return this;
    }

    @Override
    public Context getContext() {
        return _context;
    }

}
