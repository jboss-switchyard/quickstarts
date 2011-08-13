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
package org.switchyard.console.client.model;

/**
 * Transformer
 * 
 * Represents a SwitchYard configuration transform.
 * 
 * @author Rob Cernich
 */
public interface Transformer {
    /**
     * @return the from type.
     */
    public String getFrom();
    
    /**
     * @param from the from type.
     */
    public void setFrom(String from);

    /**
     * @return the to type.
     */
    public String getTo();
    
    /**
     * @param to the to type.
     */
    public void setTo(String to);

    /**
     * @return the implementation type.
     */
    public String getType();
    
    /**
     * @param type the implementation type.
     */
    public void setType(String type);

}
