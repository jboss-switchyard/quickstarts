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
package org.switchyard.console.client.ui.service;

import java.util.Collections;
import java.util.List;

import org.jboss.as.console.client.core.Places;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;
import org.switchyard.console.client.Console;
import org.switchyard.console.client.NameTokens;
import org.switchyard.console.client.model.Service;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

/**
 * GatewaysList
 * 
 * Wraps a table control for displaying a service's gateways.
 * 
 * @author Rob Cernich
 */
public class GatewaysList {

    private DefaultCellTable<String> _gatewaysTable;
    private ListDataProvider<String> _gatewaysDataProvider;

    GatewaysList() {
        _gatewaysTable = new DefaultCellTable<String>(5);

        Column<String, String> nameColumn = new Column<String, String>(new ClickableTextCell()) {
            @Override
            public String getValue(String service) {
                return service;
            }
        };
        nameColumn.setFieldUpdater(new FieldUpdater<String, String>() {
            @Override
            public void update(int index, String object, String value) {
                // TODO: see if we can do this without using a global, e.g.
                // _presenter.xxx()
                Console.MODULES.getPlaceManager().revealPlaceHierarchy(
                        Places.fromString(NameTokens.createComponentLink(value)));
            }
        });
        nameColumn.setSortable(true);

        _gatewaysTable.addColumn(nameColumn, "Gateway Type");

        _gatewaysDataProvider = new ListDataProvider<String>();
        _gatewaysDataProvider.addDataDisplay(_gatewaysTable);
    }

    /**
     * @return this object's widget.
     */
    public Widget asWidget() {
        return _gatewaysTable;
    }

    /**
     * @param service the service providing the data.
     */
    public void setService(Service service) {
        List<String> gateways = service.getGateways();
        if (gateways == null) {
            gateways = Collections.emptyList();
        }
        _gatewaysDataProvider.setList(gateways);
    }
}
