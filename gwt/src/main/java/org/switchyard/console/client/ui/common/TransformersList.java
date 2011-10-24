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

import java.util.Collections;
import java.util.List;

import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.switchyard.console.client.model.Transformer;

import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

/**
 * TransformersList
 * 
 * Wraps a table control for displaying transformers.
 * 
 * @author Rob Cernich
 */
public class TransformersList {
    private DefaultCellTable<Transformer> _transformersTable;
    private ListDataProvider<Transformer> _transformersDataProvider;

    /**
     * Create a new TransformersList.
     */
    public TransformersList() {
        _transformersTable = new DefaultCellTable<Transformer>(5);

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

        _transformersTable.addColumn(fromColumn, "From");
        _transformersTable.addColumn(toColumn, "To");
        _transformersTable.addColumn(typeColumn, "Type");

        _transformersDataProvider = new ListDataProvider<Transformer>();
        _transformersDataProvider.addDataDisplay(_transformersTable);
    }

    /**
     * @return this object's widget.
     */
    public Widget asWidget() {
        return _transformersTable;
    }

    /**
     * @param transformers the transformers to display.
     */
    public void setTransformers(List<Transformer> transformers) {
        if (transformers == null) {
            transformers = Collections.emptyList();
        }
        _transformersDataProvider.setList(transformers);
    }

}
