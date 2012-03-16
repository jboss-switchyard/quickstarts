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
package org.switchyard.common.version;

import static java.lang.System.out;
import static org.apache.log4j.Level.INFO;
import static org.switchyard.common.version.Queries.Projects.SWITCHYARD;
import static org.switchyard.common.version.Queries.Projects.SWITCHYARD_COMMON;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

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
     * @param level the Level to log at
     */
    public static void logSwitchYardNotification(Logger logger, Level level) {
        if (logger.isEnabledFor(level)) {
            logger.log(level, getSwitchYardNotification());
        }
    }

    /**
     * Logs the SwitchYard notification returned by {@link #getSwitchYardNotification()}.
     * @param logger the Logger to log to
     */
    public static void logSwitchYardNotification(Logger logger) {
        logSwitchYardNotification(logger, INFO);
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
     * @param level the Level to log at
     */
    public static void logSwitchYardVersions(Logger logger, Level level) {
        if (logger.isEnabledFor(level)) {
            for (Version version : getSwitchYardVersions()) {
                logger.log(level, version);
            }
        }
    }
    /**
     * Logs each Version returned by {@link #getSwitchYardVersions()}.
     * @param logger the Logger to log to
     */
    public static void logSwitchYardVersions(Logger logger) {
        logSwitchYardVersions(logger, INFO);
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
