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
package org.switchyard.component.common.composer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.xml.namespace.QName;

import org.switchyard.common.lang.Strings;
import org.switchyard.common.xml.XMLHelper;

/**
 * Base class for RegexContextMapper; adds the regex pattern matching ability.
 *
 * @param <D> the type of binding data
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class BaseRegexContextMapper<D extends BindingData> extends BaseContextMapper<D> implements RegexContextMapper<D> {

    private final List<Pattern> _includes = new ArrayList<Pattern>();
    private final List<Pattern> _excludes = new ArrayList<Pattern>();
    private final List<Pattern> _includeNamespaces = new ArrayList<Pattern>();
    private final List<Pattern> _excludeNamespaces = new ArrayList<Pattern>();

    private void setPatternList(String regexs, List<Pattern> patternList) {
        Set<String> regexSet = Strings.uniqueSplitTrimToNull(regexs, ",");
        List<Pattern> tmpList = new ArrayList<Pattern>();
        for (String regex : regexSet) {
            try {
                Pattern pattern = Pattern.compile(regex);
                tmpList.add(pattern);
            } catch (PatternSyntaxException pse) {
                throw new IllegalArgumentException("\"" + regex + "\" is not a valid regex pattern: " + pse.getMessage());
            }
        }
        synchronized (patternList) {
            patternList.clear();
            patternList.addAll(tmpList);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContextMapper<D> setIncludes(String includes) {
        setPatternList(includes, _includes);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContextMapper<D> setExcludes(String excludes) {
        setPatternList(excludes, _excludes);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContextMapper<D> setIncludeNamespaces(String includeNamespaces) {
        setPatternList(includeNamespaces, _includeNamespaces);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContextMapper<D> setExcludeNamespaces(String excludeNamespaces) {
        setPatternList(excludeNamespaces, _excludeNamespaces);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(String name) {
        return matches(XMLHelper.createQName(name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(QName qname) {
        return qname != null && matches(qname.getLocalPart(), _includes, _excludes) && matches(qname.getNamespaceURI(), _includeNamespaces, _excludeNamespaces);
    }

    private boolean matches(String test, List<Pattern> includes, List<Pattern> excludes) {
        boolean green = false;
        boolean red = false;
        for (Pattern include : includes) {
            if (include.matcher(test).matches()) {
                green = true;
                break;
            } else {
                red = true;
            }
        }
        boolean matches = green || !red;
        if (matches) {
            green = false;
            red = false;
            for (Pattern exclude : excludes) {
                if (!exclude.matcher(test).matches()) {
                    green = true;
                    break;
                } else {
                    red = true;
                }
            }
            matches = green || !red;
        }
        return matches;
    }

}
