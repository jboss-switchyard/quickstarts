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
package org.switchyard.console.client.ui.artifacts;

import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.switchyard.console.client.model.ArtifactReference;
import org.switchyard.console.client.ui.common.AbstractDataTable;

import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

/**
 * ArtifactReferencesList
 * 
 * Wraps a table control for displaying artifact references.
 * 
 * @author Rob Cernich
 */
public class ArtifactReferencesList extends AbstractDataTable<ArtifactReference> {

    /**
     * Create a new ArtifactReferencesList.
     */
    public ArtifactReferencesList() {
        super("Artifact References");
    }

    @Override
    protected ProvidesKey<ArtifactReference> createKeyProvider() {
        return new ProvidesKey<ArtifactReference>() {
            @Override
            public Object getKey(ArtifactReference item) {
                return item.key();
            }
        };

    }

    @SuppressWarnings("unchecked")
    @Override
    protected void createColumns(DefaultCellTable<ArtifactReference> table,
            ListDataProvider<ArtifactReference> dataProvider) {
        final TextColumn<ArtifactReference> nameColumn = new TextColumn<ArtifactReference>() {
            @Override
            public String getValue(ArtifactReference reference) {
                return reference.getName();
            }
        };
        nameColumn.setSortable(true);

        final TextColumn<ArtifactReference> urlColumn = new TextColumn<ArtifactReference>() {
            @Override
            public String getValue(ArtifactReference reference) {
                return reference.getUrl();
            }
        };
        urlColumn.setSortable(true);

        ColumnSortEvent.ListHandler<ArtifactReference> sortHandler = new ColumnSortEvent.ListHandler<ArtifactReference>(
                dataProvider.getList());
        sortHandler.setComparator(nameColumn, createColumnCommparator(nameColumn));
        sortHandler.setComparator(urlColumn, createColumnCommparator(urlColumn));

        table.addColumn(nameColumn, "Name");
        table.addColumn(urlColumn, "URL");
        table.addColumnSortHandler(sortHandler);

        table.addColumnSortHandler(sortHandler);
        table.getColumnSortList().push(urlColumn);
        table.getColumnSortList().push(nameColumn);
    }

}
