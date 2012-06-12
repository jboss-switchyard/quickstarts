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
package org.switchyard.component.common.rules.util.drools;

import org.apache.log4j.Logger;
import org.drools.SystemEventListener;
import org.switchyard.common.lang.Strings;

/**
 * A SystemEventListener that uses log4j.
 *
 * @author dward at jboss.org
 */
public class LogSystemEventListener implements SystemEventListener {

    private static final Logger LOGGER = Logger.getLogger(LogSystemEventListener.class);

    private String _messagePrefix;

    /**
     * Default constructor.
     */
    public LogSystemEventListener() {
        this(null);
    }

    /**
     * Constructor specifying a message prefix.
     * @param messagePrefix the message prefix
     */
    public LogSystemEventListener(String messagePrefix) {
        setMessagePrefix(messagePrefix);
    }

    /**
     * Gets the message prefix.
     * @return the message prefix
     */
    protected final String getMessagePrefix() {
        return _messagePrefix;
    }

    /**
     * Sets the message prefix.
     * @param messagePrefix the message prefix
     * @return this instance (useful for chaining)
     */
    protected final LogSystemEventListener setMessagePrefix(String messagePrefix) {
        messagePrefix = Strings.trimToNull(messagePrefix);
        _messagePrefix = messagePrefix != null ? messagePrefix + ": " : "";
        return this;
    }

    /**
     * Gets the correct logger.
     * @return the logger
     */
    protected Logger getLogger() {
        return LOGGER;
    }

    private final String format(String message) {
        return new StringBuilder()
            .append(getMessagePrefix())
            .append(String.valueOf(message))
            .toString();
    }

    private final String format(String message, Object object) {
        return new StringBuilder()
            .append(format(message))
            .append(" - Object[")
            .append(String.valueOf(object))
            .append("]")
            .toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void info(String message) {
        getLogger().info(format(message));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void info(String message, Object object) {
        getLogger().info(format(message, object));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void warning(String message) {
        getLogger().warn(format(message));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void warning(String message, Object object) {
        getLogger().warn(format(message, object));
    }
 
    /**
     * {@inheritDoc}
     */
    @Override
    public final void exception(Throwable e) {
        getLogger().error(format(e.getMessage()), e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void exception(String message, Throwable e) {
        getLogger().error(format(message), e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void debug(String message) {
        getLogger().debug(format(message));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void debug(String message, Object object) {
        getLogger().debug(format(message, object));
    }

}
