package org.switchyard.component.jca.config.model.v1;

import javax.xml.namespace.QName;

import org.switchyard.component.jca.JCAConstants;
import org.switchyard.component.jca.config.model.PropertyModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * V1 Property model.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class V1PropertyModel extends BaseModel implements PropertyModel {

    /**
     * Constructor.
     * 
     * @param key key of property
     * @param value value of property
     */
    public V1PropertyModel(String key, String value) {
        super(new QName(JCAConstants.DEFAULT_NAMESPACE, JCAConstants.PROPERTY));
        setModelAttribute(JCAConstants.NAME, key);
        setModelAttribute(JCAConstants.VALUE, value);
    }
    
    /**
     * Constructor.
     * 
     * @param config configuration
     * @param desc descriptor
     */
    public V1PropertyModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public String getName() {
        return getModelAttribute(JCAConstants.NAME);
    }

    @Override
    public PropertyModel setName(String name) {
        setModelAttribute(JCAConstants.NAME, name);
        return this;
    }

    @Override
    public String getValue() {
        return getModelAttribute(JCAConstants.VALUE);
    }

    @Override
    public PropertyModel setValue(String value) {
        setModelAttribute(JCAConstants.VALUE, value);
        return this;
    }

}
