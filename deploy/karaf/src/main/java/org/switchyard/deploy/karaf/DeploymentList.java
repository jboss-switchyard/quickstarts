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
package org.switchyard.deploy.karaf;

import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.osgi.framework.ServiceRegistration;
import org.switchyard.deploy.osgi.SwitchyardEvent;
import org.switchyard.deploy.osgi.SwitchyardListener;

import java.io.PrintStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 */
@Command(scope = "switchyard", name = "deployment-list", description = "List switchyard deployments.")
public class DeploymentList extends OsgiCommandSupport {


    private static final String ID_COLUMN_LABEL = "Id";
    private static final String NAME_COLUMN_LABEL = "Name";
    private static final String STATUS_COLUMN_LABEL = "Status";
    private static final int DEFAULT_FORMAT_BUFFER_LENGTH = 24;
    private static final String DEFAULT_FIELD_PREAMBLE = "[ ";
    private static final String DEFAULT_FIELD_POSTAMBLE = " ]";
    private static final String DEFAULT_HEADER_PREAMBLE = "  ";
    private static final String DEFAULT_HEADER_POSTAMBLE = "  ";
    private static final int DEFAULT_COLUMN_WIDTH_INCREMENT = 0;
    private static final int MAX_COLUMN_WIDTH = Integer.MAX_VALUE;

    private final List<SwitchyardEvent> events = new CopyOnWriteArrayList<SwitchyardEvent>();

    @Override
    protected Object doExecute() throws Exception {
        SwitchyardListener listener = new SwitchyardListener() {
            @Override
            public void switchyardEvent(SwitchyardEvent event) {
                events.add(event);
            }
        };
        ServiceRegistration<SwitchyardListener> reg = getBundleContext().registerService(SwitchyardListener.class, listener, null);
        reg.unregister();

        if (!events.isEmpty()) {
            final Map<String, Integer> columnWidths = computeColumnWidths(events);
            final String headerFormat = buildFormatString(columnWidths, true);
            final String rowFormat = buildFormatString(columnWidths, false);
            final PrintStream out = System.out;

            out.println(String.format(headerFormat, ID_COLUMN_LABEL, NAME_COLUMN_LABEL, STATUS_COLUMN_LABEL));
            for (SwitchyardEvent event : events) {
                out.println(String.format(rowFormat, getId(event), getName(event), getStatus(event)));
            }
        }
        return null;
    }

    private static Map<String, Integer> computeColumnWidths(final Iterable<SwitchyardEvent> events) throws Exception {
        if (events == null) {
            throw new IllegalArgumentException("Unable to determine column widths from null Iterable<SwitchyardEvent>");
        } else {
            int maxIdLen = 0;
            int maxNameLen = 0;
            int maxStatusLen = 0;

            for (final SwitchyardEvent event : events) {
                maxIdLen = java.lang.Math.max(maxIdLen, getId(event).length());
                maxNameLen = java.lang.Math.max(maxNameLen, getName(event).length());
                maxStatusLen = java.lang.Math.max(maxStatusLen, getStatus(event).length());
            }

            final Map<String, Integer> retval = new Hashtable<String, Integer>(3);
            retval.put(ID_COLUMN_LABEL, maxIdLen);
            retval.put(NAME_COLUMN_LABEL, maxNameLen);
            retval.put(STATUS_COLUMN_LABEL, maxStatusLen);

            return retval;
        }
    }

    private static String getId(SwitchyardEvent event) {
        return Long.toString(event.getBundle().getBundleId());
    }

    private static String getName(SwitchyardEvent event) {
        return event.getBundle().getSymbolicName();
    }

    private static String getStatus(SwitchyardEvent event) {
        switch (event.getType()) {
            case SwitchyardEvent.CREATING:     return "Creating";
            case SwitchyardEvent.GRACE_PERIOD: return "Grace Period";
            case SwitchyardEvent.CREATED:      return "Created";
            case SwitchyardEvent.DESTROYING:   return "Destroying";
            case SwitchyardEvent.DESTROYED:    return "Destroyed";
            case SwitchyardEvent.FAILURE:      return "Failure";
            default:                           return "Unknown";
        }
    }

    private static String buildFormatString(final Map<String, Integer> columnWidths, final boolean isHeader) {
        final String fieldPreamble;
        final String fieldPostamble;
        final int columnWidthIncrement;

        if (isHeader) {
            fieldPreamble = DEFAULT_HEADER_PREAMBLE;
            fieldPostamble = DEFAULT_HEADER_POSTAMBLE;
        } else {
            fieldPreamble = DEFAULT_FIELD_PREAMBLE;
            fieldPostamble = DEFAULT_FIELD_POSTAMBLE;
        }
        columnWidthIncrement = DEFAULT_COLUMN_WIDTH_INCREMENT;

        final int idLen = java.lang.Math.min(columnWidths.get(ID_COLUMN_LABEL) + columnWidthIncrement, MAX_COLUMN_WIDTH);
        final int nameLen = java.lang.Math.min(columnWidths.get(NAME_COLUMN_LABEL) + columnWidthIncrement, MAX_COLUMN_WIDTH);
        final int statusLen = java.lang.Math.min(columnWidths.get(STATUS_COLUMN_LABEL) + columnWidthIncrement, MAX_COLUMN_WIDTH);

        final StringBuilder retval = new StringBuilder(DEFAULT_FORMAT_BUFFER_LENGTH);
        retval.append(fieldPreamble).append("%-").append(idLen).append('.').append(idLen).append('s').append(fieldPostamble).append(' ');
        retval.append(fieldPreamble).append("%-").append(nameLen).append('.').append(nameLen).append('s').append(fieldPostamble).append(' ');
        retval.append(fieldPreamble).append("%-").append(statusLen).append('.').append(statusLen).append('s').append(fieldPostamble).append(' ');

        return retval.toString();
    }

}
