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
package org.switchyard.console.client.ui.common;

import org.jboss.as.console.client.shared.runtime.charts.StackedBar;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;

/**
 * PercentageBarCell
 * 
 * <p/>
 * A cell that displays a fractional value as a bar, whose fill corresponds to
 * the fractional amount.
 * 
 * @author Rob Cernich
 */
public class PercentageBarCell extends AbstractCell<Double> {

    private StackedBar _barControl;
    private Widget _widget;

    /**
     * Create a new PercentageBarCell.
     */
    public PercentageBarCell() {
        super((String[]) null);
        _barControl = new StackedBar();
        _widget = _barControl.asWidget();
    }

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, Double value, SafeHtmlBuilder sb) {
        _barControl.setRatio(1.0, value);
        sb.appendHtmlConstant(_widget.toString());
    }

}
