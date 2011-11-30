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
package org.switchyard.component.bpel.deploy;

import org.switchyard.config.Configuration;
import org.switchyard.deploy.BaseComponent;

/**
 * An implementation of BPEL component.
 */
public class BPELComponent extends BaseComponent {

    private BPELActivator _activator=new BPELActivator();
    
    /**
     * Default constructor.
     */
    public BPELComponent() {
        setName("BPELComponent");
        setActivator(_activator);
    }

    /* (non-Javadoc)
     * @see org.switchyard.deploy.Component#init(org.switchyard.config.Configuration)
     */
    @Override
    public void init(Configuration config) {
        super.init(config);
        _activator.setConfiguration(config);
    }
    
}
