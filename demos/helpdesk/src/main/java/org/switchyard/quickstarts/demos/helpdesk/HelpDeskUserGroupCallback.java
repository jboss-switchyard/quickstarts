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
package org.switchyard.quickstarts.demos.helpdesk;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.switchyard.component.bpm.runtime.BPMUserGroupCallback;

/**
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public final class HelpDeskUserGroupCallback extends BPMUserGroupCallback {

    public static final Map<String,List<String>> USERS_GROUPS;
    static {
        Map<String,List<String>> ugm = new LinkedHashMap<String,List<String>>();
        ugm.put("krisv", Arrays.asList(new String[] {"developers"}));
        ugm.put("david", Arrays.asList(new String[] {"users"}));
        USERS_GROUPS = Collections.unmodifiableMap(ugm);
    }

    private static final Properties USERS_GROUPS_PROPERTIES = new Properties();
    static {
        USERS_GROUPS_PROPERTIES.setProperty("Administrator", "users,developers");
        USERS_GROUPS_PROPERTIES.setProperty("david", "users");
        USERS_GROUPS_PROPERTIES.setProperty("krisv", "developers");
    }

    public HelpDeskUserGroupCallback() {
        super(USERS_GROUPS_PROPERTIES);
    }

}
