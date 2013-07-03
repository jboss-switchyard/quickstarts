/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.transform.smooks.internal;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.apache.log4j.Logger;
import org.milyn.Smooks;
import org.milyn.container.plugin.SourceFactory;
import org.milyn.payload.Export;
import org.milyn.payload.Exports;
import org.milyn.payload.JavaResult;
import org.milyn.payload.StringResult;
import org.switchyard.SwitchYardException;
import org.switchyard.config.model.Scannable;
import org.switchyard.transform.BaseTransformer;
import org.switchyard.transform.config.model.SmooksTransformModel;

/**
 * Smooks {@link org.switchyard.transform.Transformer}.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@Scannable(false)
public class SmooksTransformer extends BaseTransformer {

    private static Logger _log = Logger.getLogger(SmooksTransformer.class);

    private Smooks _smooks;
    private String _reportPath;
    private Export _export;

    /**
     * Constructor.
     * @param from From type.
     * @param to To type.
     * @param smooks Smooks instance.
     */
    protected SmooksTransformer(final QName from, final QName to, Smooks smooks, SmooksTransformModel model) {
        super(from, to);
        _smooks = smooks;
        _reportPath = model.getReportPath();
        init(model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object transform(Object from) {
        if (from == null) {
            _log.debug("Null from payload passed to SmooksTransformer.  Returning null.");
            return null;
        }

        Source source = SourceFactory.getInstance().createSource(from);
        if (_export != null) {
            Result result = newResultInstance();
            _smooks.filterSource(source, result);
            return extractResultData(result);
        } else {
            _smooks.filterSource(source);
            return from;
        }
    }

    /**
     * Set the report path.
     * <p/>
     * Only use for debugging purposes.
     *
     * @param reportPath Report path.
     */
    public void setReportPath(String reportPath) {
        this._reportPath = reportPath;
    }

    private void init(SmooksTransformModel model) {
        Exports exports = Exports.getExports(_smooks.getApplicationContext());

        // Must define 1 exported result type
        if (exports == null) {
            _log.debug("Smooks configuration '" + model.getConfig() + "'will not make updates to the Exchange Message payload because it does not define any <core:exports>.  See Smooks User Guide.");
            return;
        }
        if (exports.getExports().size() != 1) {
            throw new SwitchYardException("Invalid Smooks configuration file.  Must define an <core:exports> section with a single <core:export>.  See Smooks User Guide.");
        }

        _export = exports.getExports().iterator().next();

        // Only support String (character based) or Java Results for now...
        Class<?> exportType = _export.getType();
        if (StringResult.class.isAssignableFrom(exportType)) {
            return;
        } else if (JavaResult.class.isAssignableFrom(exportType)) {
            return;
        }

        throw new SwitchYardException("Unsupported Smooks <core:export> type '" + exportType.getName() + "'.  Only supports StringResult or JavaResult.");
    }

    private Result newResultInstance() {
        Class<?> resultType = _export.getType();
        try {
            return (Result) resultType.newInstance();
        } catch (Exception e) {
            throw new SwitchYardException("Unexpected exception while creating an instance of Result type '" + resultType.getName() + "'.", e);
        }
    }

    private Object extractResultData(Result result) {
        if (result instanceof StringResult) {
            return ((StringResult) result).getResult();
        } else if (result instanceof JavaResult) {
            return ((JavaResult) result).extractFromResult((JavaResult) result, _export);
        }
        return null;
    }
}
