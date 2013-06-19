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
package org.switchyard.deploy.osgi;

/**
 */
public class EventConstants {

    private EventConstants() {
        // non-instantiable class
    }

    /**
     * The type of the event that has been issued. This property is of type
     * <code>Integer</code> and can take one of the values defined in
     * {@link SwitchyardEvent}.
     */
    public static final String TYPE = "type";

    /**
     * The <code>SwitchyardEvent</code> object that caused this event. This
     * property is of type {@link SwitchyardEvent}.
     */
    public static final String EVENT = "event";

    /**
     * The time the event was created. This property is of type
     * <code>Long</code>.
     */
    public static final String TIMESTAMP = "timestamp";

    /**
     * The Switchyard bundle associated with this event. This property is of type
     * <code>Bundle</code>.
     */
    public static final String BUNDLE = "bundle";

    /**
     * The bundle id of the Switchyard bundle associated with this event. This
     * property is of type <code>Long</code>.
     */
    public static final String BUNDLE_ID = "bundle.id";

    /**
     * The bundle symbolic name of the Switchyard bundle associated with this
     * event. This property is of type <code>String</code>.
     */
    public static final String BUNDLE_SYMBOLICNAME = "bundle.symbolicName";

    /**
     * The bundle version of the Switchyard bundle associated with this event.
     * This property is of type <code>Version</code>.
     */
    public static final String BUNDLE_VERSION = "bundle.version";

    /**
     * The Switchyard extender bundle that is generating this event. This
     * property is of type <code>Bundle</code>.
     */
    public static final String EXTENDER_BUNDLE = "extender.bundle";

    /**
     * The bundle id of the Switchyard extender bundle that is generating this
     * event. This property is of type <code>Long</code>.
     */
    public static final String EXTENDER_BUNDLE_ID = "extender.bundle.id";

    /**
     * The bundle symbolic of the Switchyard extender bundle that is generating
     * this event. This property is of type <code>String</code>.
     */
    public static final String EXTENDER_BUNDLE_SYMBOLICNAME = "extender.bundle.symbolicName";

    /**
     * The bundle version of the Switchyard extender bundle that is generating
     * this event. This property is of type <code>Version</code>.
     */
    public static final String EXTENDER_BUNDLE_VERSION = "extender.bundle.version";

    /**
     * The filters identifying the missing dependencies that caused this event
     * for a {@link SwitchyardEvent#FAILURE FAILURE} or
     * {@link SwitchyardEvent#GRACE_PERIOD GRACE_PERIOD}. This property type is an
     * array of <code>String</code>.
     */
    public static final String DEPENDENCIES = "dependencies";

    /**
     * The cause for a {@link SwitchyardEvent#FAILURE FAILURE} event. This
     * property is of type <code>Throwable</code>.
     */
    public static final String CAUSE = "cause";

    /**
     * Topic prefix for all events issued by the Switchyard Container
     */
    public static final String TOPIC_SWITCHYARD_EVENTS = "org/switchyard/container";

    /**
     * Topic for Switchyard Container CREATING events
     */
    public static final String TOPIC_CREATING = TOPIC_SWITCHYARD_EVENTS
            + "/CREATING";

    /**
     * Topic for Switchyard Container CREATED events
     */
    public static final String TOPIC_CREATED = TOPIC_SWITCHYARD_EVENTS
            + "/CREATED";

    /**
     * Topic for Switchyard Container DESTROYING events
     */
    public static final String TOPIC_DESTROYING = TOPIC_SWITCHYARD_EVENTS
            + "/DESTROYING";

    /**
     * Topic for Switchyard Container DESTROYED events
     */
    public static final String TOPIC_DESTROYED = TOPIC_SWITCHYARD_EVENTS
            + "/DESTROYED";

    /**
     * Topic for Switchyard Container FAILURE events
     */
    public static final String TOPIC_FAILURE = TOPIC_SWITCHYARD_EVENTS
            + "/FAILURE";

    /**
     * Topic for Switchyard Container GRACE_PERIOD events
     */
    public static final String TOPIC_GRACE_PERIOD = TOPIC_SWITCHYARD_EVENTS
            + "/GRACE_PERIOD";

}
