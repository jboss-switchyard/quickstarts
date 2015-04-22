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
package org.switchyard.component.common.label;

import org.switchyard.label.Label;

/**
 * Represents endpoint labels.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public enum EndpointLabel implements Label {

    /** Endpoint labels. */
    AMQP, ATOM, DIRECT, FILE, FTP, FTPS, HTTP, JCA, JMS, JPA, MAIL, MOCK, QUARTZ, REST, RSS, SEDA, SFTP, SOAP, SQL, TCP, TIMER, UDP, URI;

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
