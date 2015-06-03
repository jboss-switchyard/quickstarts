package org.switchyard.component.jca.config.model.v1;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;

/**
 * Generic configuration interface used to represent a basic name to value mapping
 * for a child configuration, e.g.
 * <br><br>
 * <pre>
 *    <name>value</name>
 * </pre>
 * <br>
 */
public class V1NameValueModel extends BaseModel {
    
    /**
     * Create a new NameValueModel with the specified name.
     * @param namespace namespace
     * @param name config name
     */
    public V1NameValueModel(String namespace, String name) {
        super(namespace, name);
    }
    
    /**
     * Create a new NameValueModel based on an existing config element.
     * @param config configuration element
     */
    public V1NameValueModel(Configuration config) {
        super(config);
    }
    
    /**
     * Get the config value.
     * @return config value
     */
    public String getValue() {
        return super.getModelValue();
    }
    
    /**
     * Set the config value.
     * @param value config value
     */
    public void setValue(String value) {
        super.setModelValue(value);
    }
}

