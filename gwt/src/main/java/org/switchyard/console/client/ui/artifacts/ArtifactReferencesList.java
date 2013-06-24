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
