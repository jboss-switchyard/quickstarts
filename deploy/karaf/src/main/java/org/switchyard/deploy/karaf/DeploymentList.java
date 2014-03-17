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
package org.switchyard.deploy.karaf;

import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.osgi.framework.ServiceRegistration;
import org.switchyard.deploy.osgi.SwitchYardEvent;
import org.switchyard.deploy.osgi.SwitchYardListener;

import java.io.PrintStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Shell commands for deployments.
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

    private final List<SwitchYardEvent> _events = new CopyOnWriteArrayList<SwitchYardEvent>();

    @Override
    protected Object doExecute() throws Exception {
        SwitchYardListener listener = new SwitchYardListener() {
            @Override
            public void switchyardEvent(SwitchYardEvent event) {
                _events.add(event);
            }
        };
        ServiceRegistration<SwitchYardListener> reg = getBundleContext().registerService(SwitchYardListener.class, listener, null);
        reg.unregister();

        if (!_events.isEmpty()) {
            final Map<String, Integer> columnWidths = computeColumnWidths(_events);
            final String headerFormat = buildFormatString(columnWidths, true);
            final String rowFormat = buildFormatString(columnWidths, false);
            final PrintStream out = System.out;

            out.println(String.format(headerFormat, ID_COLUMN_LABEL, NAME_COLUMN_LABEL, STATUS_COLUMN_LABEL));
            for (SwitchYardEvent event : _events) {
                out.println(String.format(rowFormat, getId(event), getName(event), getStatus(event)));
            }
        }
        return null;
    }

    private static Map<String, Integer> computeColumnWidths(final Iterable<SwitchYardEvent> events) throws Exception {
        if (events == null) {
            throw new IllegalArgumentException("Unable to determine column widths from null Iterable<SwitchyardEvent>");
        } else {
            int maxIdLen = 0;
            int maxNameLen = 0;
            int maxStatusLen = 0;

            for (final SwitchYardEvent event : events) {
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

    private static String getId(SwitchYardEvent event) {
        return Long.toString(event.getBundle().getBundleId());
    }

    private static String getName(SwitchYardEvent event) {
        return event.getBundle().getSymbolicName();
    }

    private static String getStatus(SwitchYardEvent event) {
        switch (event.getType()) {
            case SwitchYardEvent.CREATING:     return "Creating";
            case SwitchYardEvent.GRACE_PERIOD: return "Grace Period";
            case SwitchYardEvent.CREATED:      return "Created";
            case SwitchYardEvent.DESTROYING:   return "Destroying";
            case SwitchYardEvent.DESTROYED:    return "Destroyed";
            case SwitchYardEvent.FAILURE:      return "Failure";
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
