package org.switchyard.component.clojure.config.model.v1;

import javax.xml.namespace.QName;

import org.switchyard.component.clojure.config.model.ClojureComponentImplementationModel;
import org.switchyard.component.clojure.config.model.ClojureScriptModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseNamedModel;
import org.switchyard.config.model.Descriptor;

/**
 * A version 1 implementation of a ScriptModel.
 * 
 * @author Daniel Bevenius
 *
 */
public class V1ClojureScriptModel extends BaseNamedModel implements ClojureScriptModel {
    
    private String _script;
    
    /**
     * No-args constructor.
     */
    public V1ClojureScriptModel() {
        super(new QName(ClojureComponentImplementationModel.DEFAULT_NAMESPACE, SCRIPT));
    }
    
    /**
     * Constructor.
     * 
     * @param config The configuration model.
     * @param desc The descriptor for this model.
     */
    public V1ClojureScriptModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }
    
    /* (non-Javadoc)
     * @see org.switchyard.component.clojure.config.model.v1.ClojureScriptModel#getScript()
     */
    @Override
    public String getScript() {
        if (_script != null) {
            return _script;
        }
        
        _script = getModelValue();
        return _script;
    }
    
    /* (non-Javadoc)
     * @see org.switchyard.component.clojure.config.model.v1.ClojureScriptModel#setScript(java.lang.String)
     */
    @Override
    public ClojureScriptModel setScript(final String script) {
        setModelValue(script);
        _script = script;
        return this;
    }

}
