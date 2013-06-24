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
