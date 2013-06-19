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

import org.osgi.framework.Bundle;

/**
 */
public class SwitchyardEvent {

    public static final int	CREATING		= 1;
    public static final int	CREATED			= 2;
    public static final int	DESTROYING		= 3;
    public static final int	DESTROYED		= 4;
    public static final int	FAILURE			= 5;
    public static final int	GRACE_PERIOD	= 6;

    private final int		type;
    /**
     * The time when the event occurred.
     *
     * @see #getTimestamp()
     */
    private final long		timestamp;
    /**
     * The Switchyard bundle.
     *
     * @see #getBundle()
     */
    private final Bundle bundle;
    /**
     * The Switchyard extender bundle.
     *
     * @see #getExtenderBundle()
     */
    private final Bundle	extenderBundle;
    /**
     * An array containing filters identifying the missing dependencies. Must
     * not be <code>null</code> when the event type requires it.
     *
     * @see #getDependencies()
     */
    private final String[]	dependencies;
    /**
     * Cause of the failure.
     *
     * @see #getCause()
     */
    private final Throwable	cause;
    /**
     * Indicate if this event is a replay event or not.
     *
     * @see #isReplay()
     */
    private final boolean	replay;

    /**
     * Create a simple <code>SwitchyardEvent</code> object.
     *
     * @param type The type of this event.
     * @param bundle The Switchyard bundle associated with this event. This
     *        parameter must not be <code>null</code>.
     * @param extenderBundle The Switchyard extender bundle that is generating
     *        this event. This parameter must not be <code>null</code>.
     */
    public SwitchyardEvent(int type, Bundle bundle, Bundle extenderBundle) {
        this(type, bundle, extenderBundle, null, null);
    }

    /**
     * Create a <code>SwitchyardEvent</code> object associated with a set of
     * dependencies.
     *
     * @param type The type of this event.
     * @param bundle The Switchyard bundle associated with this event. This
     *        parameter must not be <code>null</code>.
     * @param extenderBundle The Switchyard extender bundle that is generating
     *        this event. This parameter must not be <code>null</code>.
     * @param dependencies An array of <code>String</code> filters for each
     *        dependency associated with this event. Must be a non-empty array
     *        for event types {@link #FAILURE}, {@link #GRACE_PERIOD}.
     *        Must be <code>null</code> for other event types.
     */
    public SwitchyardEvent(int type, Bundle bundle, Bundle extenderBundle,
                          String[] dependencies) {
        this(type, bundle, extenderBundle, dependencies, null);
    }

    /**
     * Create a <code>SwitchyardEvent</code> object associated with a failure
     * cause.
     *
     * @param type The type of this event.
     * @param bundle The Switchyard bundle associated with this event. This
     *        parameter must not be <code>null</code>.
     * @param extenderBundle The Switchyard extender bundle that is generating
     *        this event. This parameter must not be <code>null</code>.
     * @param cause A <code>Throwable</code> object describing the root cause of
     *        the event. May be <code>null</code>.
     */
    public SwitchyardEvent(int type, Bundle bundle, Bundle extenderBundle,
                          Throwable cause) {
        this(type, bundle, extenderBundle, null, cause);
    }

    /**
     * Create a <code>SwitchyardEvent</code> object associated with a failure
     * cause and a set of dependencies.
     *
     * @param type The type of this event.
     * @param bundle The Switchyard bundle associated with this event. This
     *        parameter must not be <code>null</code>.
     * @param extenderBundle The Switchyard extender bundle that is generating
     *        this event. This parameter must not be <code>null</code>.
     * @param dependencies An array of <code>String</code> filters for each
     *        dependency associated with this event. Must be a non-empty array
     *        for event type {@link #GRACE_PERIOD}.  It
     *        is optional for {@link #FAILURE} event types.
     *        Must be <code>null</code> for other event types.
     * @param cause A <code>Throwable</code> object describing the root cause of
     *        this event. May be <code>null</code>.
     */
    public SwitchyardEvent(int type, Bundle bundle, Bundle extenderBundle,
                          String[] dependencies, Throwable cause) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
        this.bundle = bundle;
        this.extenderBundle = extenderBundle;
        this.dependencies = dependencies == null ? null
                : (String[]) dependencies.clone();;
        this.cause = cause;
        this.replay = false;
        if (bundle == null) {
            throw new NullPointerException("bundle must not be null");
        }
        if (extenderBundle == null) {
            throw new NullPointerException("extenderBundle must not be null");
        }
        switch (type) {
            case GRACE_PERIOD :
                if (dependencies == null) {
                    throw new NullPointerException(
                            "dependencies must not be null");
                }
                if (dependencies.length == 0) {
                    throw new IllegalArgumentException(
                            "dependencies must not be length zero");
                }
                break;
            case FAILURE :
                // not all FAILURE events have a dependency list, but if there
                // is one, it must be non-empty.
                if (dependencies != null) {
                    if (dependencies.length == 0) {
                        throw new IllegalArgumentException(
                                "dependencies must not be length zero");
                    }
                }
                break;
            default :
                if (dependencies != null) {
                    throw new IllegalArgumentException(
                            "dependencies must be null");
                }
                break;
        }
    }

    /**
     * Create a new <code>SwitchyardEvent</code> from the specified
     * <code>SwitchyardEvent</code>. The <code>timestamp</code> property will be
     * copied from the original event and only the replay property will be
     * overridden with the given value.
     *
     * @param event The original <code>SwitchyardEvent</code> to copy. Must not
     *        be <code>null</code>.
     * @param replay <code>true</code> if this event should be used as a replay
     *        event.
     */
    public SwitchyardEvent(SwitchyardEvent event, boolean replay) {
        this.type = event.type;
        this.timestamp = event.timestamp;
        this.bundle = event.bundle;
        this.extenderBundle = event.extenderBundle;
        this.dependencies = event.dependencies;
        this.cause = event.cause;
        this.replay = replay;
    }

    /**
     * Return the type of this event.
     * <p>
     * The type values are:
     * <ul>
     * <li>{@link #CREATING}</li>
     * <li>{@link #CREATED}</li>
     * <li>{@link #DESTROYING}</li>
     * <li>{@link #DESTROYED}</li>
     * <li>{@link #FAILURE}</li>
     * <li>{@link #GRACE_PERIOD}</li>
     * </ul>
     *
     * @return The type of this event.
     */
    public int getType() {
        return type;
    }

    /**
     * Return the time at which this event was created.
     *
     * @return The time at which this event was created.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Return the Blueprint bundle associated with this event.
     *
     * @return The Blueprint bundle associated with this event.
     */
    public Bundle getBundle() {
        return bundle;
    }

    /**
     * Return the Blueprint extender bundle that is generating this event.
     *
     * @return The Blueprint extender bundle that is generating this event.
     */
    public Bundle getExtenderBundle() {
        return extenderBundle;
    }

    /**
     * Return the filters identifying the missing dependencies that caused this
     * event.
     *
     * @return The filters identifying the missing dependencies that caused this
     *         event if the event type is one of
     *         {@link #GRACE_PERIOD} or {@link #FAILURE} or <code>null</code>
     *         for the other event types.
     */
    public String[] getDependencies() {
        return dependencies == null ? null : (String[]) dependencies.clone();
    }

    /**
     * Return the cause for this {@link #FAILURE} event.
     *
     * @return The cause of the failure for this event. May be <code>null</code>
     *         .
     */
    public Throwable getCause() {
        return cause;
    }

    /**
     * Return whether this event is a replay event.
     *
     * @return <code>true</code> if this event is a replay event and
     *         <code>false</code> otherwise.
     */
    public boolean isReplay() {
        return replay;
    }
}
