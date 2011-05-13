package org.switchyard.transform.json.internal;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.namespace.QName;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.switchyard.transform.BaseTransformer;

public class Java2JSONTransformer extends BaseTransformer {

    private ObjectMapper _mapper;
    private Class _clazz;
    
	public Java2JSONTransformer(QName from, QName to, ObjectMapper mapper, Class clazz) {
        super(from, to);
        this._mapper = mapper;
        this._clazz = clazz;
	}

	@Override
	public Object transform(Object from) {
		
        try {
        	StringWriter writer = new StringWriter();
        	
        	if(_clazz.isInstance(from)){
        		_mapper.writeValue(writer, from);
        		return writer.toString();
        	}
        	else
        		throw new RuntimeException("The object to transform is of wrong instance type " + from.getClass());
        }catch(JsonProcessingException e) {
        	throw new RuntimeException("Unexpected JSON processing exception, check your transformer configuration", e);
		} catch (IOException e) {
			throw new RuntimeException("Unexpected I/O exception, check your transformer configuration", e);
		} 
	}
}
