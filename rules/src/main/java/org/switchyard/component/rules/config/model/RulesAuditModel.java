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
package org.switchyard.component.rules.config.model;

import java.io.File;

import org.switchyard.component.rules.common.RulesAuditType;
import org.switchyard.config.model.Model;

/**
 * RulesAuditModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface RulesAuditModel extends Model {

    /**
     * The rulesAudit XML element.
     */
    public static final String RULES_AUDIT = "rulesAudit";

    /**
     * Gets the interval of the audit.
     * @return the interval of the audit
     */
    public Integer getInterval();

    /**
     * Sets the interval of the audit.
     * @param interval the interval of the audit
     * @return this RulesAuditModel (useful for chaining)
     */
    public RulesAuditModel setInterval(Integer interval);

    /**
     * Gets the file of the audit.
     * @return the file of the audit
     */
    public File getFile();

    /**
     * Sets the file of the audit.
     * @param file the file of the audit
     * @return this RulesAuditModel (useful for chaining)
     */
    public RulesAuditModel setFile(File file);
 
    /**
     * Gets the type of the audit.
     * @return the type of the audit
     */
    public RulesAuditType getType();
 
    /**
     * Sets the type of the audit.
     * @param type the type of the audit
     * @return this RulesAuditModel (useful for chaining)
     */
    public RulesAuditModel setType(RulesAuditType type);

}
