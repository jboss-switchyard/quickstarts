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
package org.switchyard.common.version;

import static java.lang.System.out;
import static org.switchyard.common.version.Queries.Projects.SWITCHYARD;
import static org.switchyard.common.version.Queries.Projects.SWITCHYARD_COMMON;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;

import org.jboss.logging.Logger;

/**
 * Versions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2012 Red Hat Inc.
 */
public final class Versions {

    private static final Logger LOGGER = Logger.getLogger(Versions.class);

    private static final String NOTIFICATION_PREFIX = "SwitchYard version ";

    private Versions() {}

    /**
     * Gets the simple SwitchYard version String (based on the switchyard-common Project Version).
     * @return the simple SwitchYard version String
     */
    public static String getSwitchYardVersion() {
        return VersionFactory.instance().getVersion(SWITCHYARD_COMMON).getProject().getVersion();
    }

    /**
     * Gets the SwitchYard notification (for example, "SwitchYard version 1.0.0.Final").
     * @return the SwitchYard notification
     */
    public static String getSwitchYardNotification() {
        return NOTIFICATION_PREFIX + getSwitchYardVersion();
    }

    /**
     * Logs the SwitchYard notification returned by {@link #getSwitchYardNotification()}.
     * @param logger the Logger to log to
     */
    public static void logSwitchYardNotification(Logger logger) {
        if (logger.isInfoEnabled()) {
            logger.info(getSwitchYardNotification());
        }
    }

    /**
     * Logs the SwichYard notification returned by {@link #getSwitchYardNotification()}.
     */
    public static void logSwitchYardNotification() {
        logSwitchYardNotification(LOGGER);
    }

    /**
     * Prints the SwitchYard notification returned by {@link #getSwitchYardNotification()}.
     * @param w the Writer to print to
     */
    public static void printSwitchYardNotification(Writer w) {
        PrintWriter pw = (w instanceof PrintWriter) ? (PrintWriter)w : new PrintWriter(w);
        pw.println(getSwitchYardNotification());
        pw.flush();
    }

    /**
     * Prints the SwitchYard notification returned by {@link #getSwitchYardNotification()}.
     * @param os the OutputStream to print to
     */
    public static void printSwitchYardNotification(OutputStream os) {
        printSwitchYardNotification(new PrintWriter(os));
    }

    /**
     * Prints the SwitchYard notification returned by {@link #getSwitchYardNotification()} to STDOUT (System.out).
     */
    public static void printSwitchYardNotification() {
        printSwitchYardNotification(out);
    }

    /**
     * Gets the Set of all SwitchYard Versions (those with Project groupIds matching "org.switchyard").
     * @return the Set of all SwitchYard Versions
     */
    public static Set<Version> getSwitchYardVersions() {
        return VersionFactory.instance().getVersions(SWITCHYARD);
    }

    /**
     * Logs each Version returned by {@link #getSwitchYardVersions()}.
     * @param logger the Logger to log to
     */
    public static void logSwitchYardVersions(Logger logger) {
        if (logger.isInfoEnabled()) {
            for (Version version : getSwitchYardVersions()) {
                logger.info(version);
            }
        }
    }
    
    /**
     * Logs each Version returned by {@link #getSwitchYardVersions()}.
     */
    public static void logSwitchYardVersions() {
        logSwitchYardVersions(LOGGER);
    }

    /**
     * Prints each Version returned by {@link #getSwitchYardVersions()}.
     * @param w the Writer to print to
     */
    public static void printSwitchYardVersions(Writer w) {
        PrintWriter pw = (w instanceof PrintWriter) ? (PrintWriter)w : new PrintWriter(w);
        for (Version version : getSwitchYardVersions()) {
            pw.println(version);
        }
        pw.flush();
    }

    /**
     * Prints each Version returned by {@link #getSwitchYardVersions()}.
     * @param os the OutputStream to print to
     */
    public static void printSwitchYardVersions(OutputStream os) {
        printSwitchYardVersions(new PrintWriter(os));
    }

    /**
     * Prints each Version returned by {@link #getSwitchYardVersions()} to STDOUT (System.out).
     */
    public static void printSwitchYardVersions() {
        printSwitchYardVersions(out);
    }

    /**
     * Calls {@link #printSwitchYardNotification()} and {@link #printSwitchYardVersions()}.
     * @param args unused
     */
    public static void main(String... args) {
        printSwitchYardNotification();
        printSwitchYardVersions();
    }

}
