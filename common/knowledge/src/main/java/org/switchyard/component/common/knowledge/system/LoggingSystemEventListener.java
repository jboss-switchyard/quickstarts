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
package org.switchyard.component.common.knowledge.system;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.kie.SystemEventListener;

/**
 * A SystemEventListener that uses log4j.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class LoggingSystemEventListener implements SystemEventListener {

    private static final Logger LOGGER = Logger.getLogger(LoggingSystemEventListener.class);

    /**
     * Default constructor.
     */
    public LoggingSystemEventListener() {}

    private String format(String message) {
        return String.valueOf(message);
    }

    private String format(String message, Object object) {
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
        /*
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(format(message));
        }
        */
        debug(message); // squelch to DEBUG
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void info(String message, Object object) {
        /*
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(format(message, object));
        }
        */
        debug(message, object); // squelch to DEBUG
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void warning(String message) {
        if (LOGGER.isEnabledFor(Level.WARN)) {
            LOGGER.warn(format(message));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void warning(String message, Object object) {
        if (LOGGER.isEnabledFor(Level.WARN)) {
            LOGGER.warn(format(message, object));
        }
    }
 
    /**
     * {@inheritDoc}
     */
    @Override
    public final void exception(Throwable e) {
        if (LOGGER.isEnabledFor(Level.ERROR)) {
            LOGGER.error(format(e.getMessage()), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void exception(String message, Throwable e) {
        if (LOGGER.isEnabledFor(Level.ERROR)) {
            LOGGER.error(format(message), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void debug(String message) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(format(message));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void debug(String message, Object object) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(format(message, object));
        }
    }

}
