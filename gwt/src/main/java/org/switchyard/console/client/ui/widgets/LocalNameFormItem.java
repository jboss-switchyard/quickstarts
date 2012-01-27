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
package org.switchyard.console.client.ui.widgets;

import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.switchyard.console.client.NameTokens;

/**
 * LocalNameFormItem
 * 
 * Unwraps the local value of a qname string.
 * 
 * @author Rob Cernich
 */
public class LocalNameFormItem extends TextItem {

    private String _value;

    /**
     * Create a new LocalNameFormItem.
     * 
     * @param name the property name.
     * @param title the display text.
     */
    public LocalNameFormItem(String name, String title) {
        super(name, title);
    }

    @Override
    public String getValue() {
        return _value;
    }

    @Override
    public void clearValue() {
        _value = null;
        super.clearValue();
    }

    @Override
    public void setValue(String value) {
        _value = value;
        super.setValue(NameTokens.parseQName(value)[1]);
    }

    @Override
    public void setEnabled(boolean b) {
        // not editable
    }

}
