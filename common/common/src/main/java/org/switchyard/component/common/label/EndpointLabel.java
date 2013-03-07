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
package org.switchyard.component.common.label;

import org.switchyard.label.Label;

/**
 * Represents endpoint labels.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public enum EndpointLabel implements Label {

    /** Endpoint labels. */
    AMQP, ATOM, DIRECT, FILE, FTP, FTPS, HTTP, JCA, JMS, JPA, MAIL, MOCK, QUARTZ, REST, SEDA, SFTP, SOAP, SQL, TCP, TIMER, UDP, URI;

    private final String _label;

    private EndpointLabel() {
        _label = Label.Util.toSwitchYardLabel("endpoint", name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String label() {
        return _label;
    }

    /**
     * Gets the EndpointLabel enum via case-insensitive short-name.
     * @param name the case-insensitive short-name
     * @return the EndpointLabel enum
     */
    public static final EndpointLabel ofName(String name) {
        return Label.Util.ofName(EndpointLabel.class, name);
    }

    /**
     * Gets the full-form endpoint label from the case-insensitive short-name.
     * @param name the case-insensitive short-name
     * @return the full-form endpoint label
     */
    public static final String toLabel(String name) {
        EndpointLabel label = ofName(name);
        return label != null ? label.label() : null;
    }

    /**
     * Prints all known endpoint labels.
     * @param args ignored
     */
    public static void main(String... args) {
        Label.Util.print(values());
    }

}
