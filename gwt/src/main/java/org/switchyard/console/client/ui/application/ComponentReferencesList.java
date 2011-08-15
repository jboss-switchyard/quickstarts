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
package org.switchyard.console.client.ui.application;

import java.util.Collections;
import java.util.List;

import org.jboss.as.console.client.widgets.tables.DefaultCellTable;
import org.switchyard.console.client.NameTokens;
import org.switchyard.console.client.model.ComponentReference;
import org.switchyard.console.client.model.ComponentService;

import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

/**
 * ComponentReferencesList
 * 
 * Wraps a table control for displaying a service's references.
 * 
 * @author Rob Cernich
 */
public class ComponentReferencesList {

    private static final ProvidesKey<ComponentReference> KEY_PROVIDER = new ProvidesKey<ComponentReference>() {
        @Override
        public Object getKey(ComponentReference item) {
            return item.getName();
        }
    };

    private DefaultCellTable<ComponentReference> _referencesTable;
    private ListDataProvider<ComponentReference> _referencesDataProvider;

    ComponentReferencesList() {
        _referencesTable = new DefaultCellTable<ComponentReference>(5);

        TextColumn<ComponentReference> nameColumn = new TextColumn<ComponentReference>() {
            @Override
            public String getValue(ComponentReference reference) {
                return NameTokens.parseQName(reference.getName())[1];
            }
        };
        nameColumn.setSortable(true);

        TextColumn<ComponentReference> interfaceColumn = new TextColumn<ComponentReference>() {
            @Override
            public String getValue(ComponentReference reference) {
                return reference.getInterface();
            }
        };
        interfaceColumn.setSortable(true);

        _referencesTable.addColumn(nameColumn, "Name");
        _referencesTable.addColumn(interfaceColumn, "Interface");

        _referencesDataProvider = new ListDataProvider<ComponentReference>(KEY_PROVIDER);
        _referencesDataProvider.addDataDisplay(_referencesTable);
    }

    /**
     * @return this object's widget.
     */
    public Widget asWidget() {
        return _referencesTable;
    }

    /**
     * @param service the service providing the data.
     */
    public void setService(ComponentService service) {
        List<ComponentReference> references = service.getReferences();
        if (references == null) {
            references = Collections.emptyList();
        }
        _referencesDataProvider.setList(references);
    }
}
