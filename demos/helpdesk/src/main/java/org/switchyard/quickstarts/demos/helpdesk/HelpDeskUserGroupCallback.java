/*
 * 2012 Red Hat Inc. and/or its affiliates and other contributors.
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
