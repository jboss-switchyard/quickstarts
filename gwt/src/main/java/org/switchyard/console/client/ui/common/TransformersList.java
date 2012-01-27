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
package org.switchyard.console.client.ui.common;

import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.switchyard.console.client.model.Transformer;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

/**
 * TransformersList
 * 
 * Wraps a table control for displaying transformers.
 * 
 * @author Rob Cernich
 */
public class TransformersList extends AbstractDataTable<Transformer> {

    /**
     * Create a new TransformersList.
     */
    public TransformersList() {
        super("Transformers");
    }

    @Override
    protected void createColumns(DefaultCellTable<Transformer> table, ListDataProvider<Transformer> dataProvider) {
        TextColumn<Transformer> fromColumn = new TextColumn<Transformer>() {
            @Override
            public String getValue(Transformer transform) {
                return transform.getFrom();
            }
        };
        fromColumn.setSortable(true);

        TextColumn<Transformer> toColumn = new TextColumn<Transformer>() {
            @Override
            public String getValue(Transformer transform) {
                return transform.getTo();
            }
        };
        toColumn.setSortable(true);

        TextColumn<Transformer> typeColumn = new TextColumn<Transformer>() {
            @Override
            public String getValue(Transformer transform) {
                return transform.getType();
            }
        };
        typeColumn.setSortable(true);

        ColumnSortEvent.ListHandler<Transformer> sortHandler = new ColumnSortEvent.ListHandler<Transformer>(
                dataProvider.getList());
        sortHandler.setComparator(fromColumn, createColumnCommparator(fromColumn));
        sortHandler.setComparator(toColumn, createColumnCommparator(toColumn));
        sortHandler.setComparator(typeColumn, createColumnCommparator(typeColumn));

        table.addColumn(fromColumn, "From");
        table.addColumn(toColumn, "To");
        table.addColumn(typeColumn, "Type");

        table.addColumnSortHandler(sortHandler);
        table.getColumnSortList().push(typeColumn);
        table.getColumnSortList().push(toColumn);
        table.getColumnSortList().push(fromColumn);

        table.setWidth("100%", false);
        table.setColumnWidth(fromColumn, 45, Style.Unit.PCT);
        table.setColumnWidth(toColumn, 45, Style.Unit.PCT);
        table.setColumnWidth(typeColumn, 10, Style.Unit.PCT);
    }

    @Override
    protected ProvidesKey<Transformer> createKeyProvider() {
        return new ProvidesKey<Transformer>() {
            @Override
            public Object getKey(Transformer item) {
                return item.getType() + ":" + item.getFrom() + ":" + item.getTo();
            }
        };
    }

}
