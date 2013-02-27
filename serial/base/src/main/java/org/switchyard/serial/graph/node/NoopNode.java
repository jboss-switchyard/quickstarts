/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.serial.graph.node;

import org.switchyard.serial.graph.Graph;

/**
 * A node representing a noop.
 * 
 * Note that there is a noop property, however it isn't used.  It only exists so serializer implementations (like Jackson or Protostuff) don't complain about "no properties".
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
@SuppressWarnings("serial")
public class NoopNode implements Node {

    /**
     * A single instance of NoopNode.
     */
    public static final NoopNode INSTANCE = new NoopNode();

    private String _noop;

    /**
     * Gets the noop.
     * @return the noop
     */
    public String getNoop() {
        return _noop;
    }

    /**
     * Sets the noop.
     * @param noop the noop
     */
    public void setNoop(String noop) {
        _noop = noop;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void compose(Object obj, Graph graph) {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object decompose(Graph graph) {
        // noop
        return null;
    }

}
