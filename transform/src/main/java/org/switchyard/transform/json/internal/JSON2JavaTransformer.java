package org.switchyard.transform.json.internal;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.switchyard.transform.BaseTransformer;

public class JSON2JavaTransformer extends BaseTransformer {

    private ObjectMapper _mapper;
    private Class _clazz;
    private QName _from;
    private QName _to;
    
	public JSON2JavaTransformer(QName from, QName to, ObjectMapper mapper, Class clazz) {
        super(from, to);
        this._from = from;
        this._to = to;
        this._mapper = mapper;
        this._clazz = clazz;
	}

	@Override
	public Object transform(Object from) {
		
		try {
			Object result = _mapper.readValue((String) from, _clazz);
			
			if(_clazz.isInstance(result))
				return result;
			else 
				throw new RuntimeException("Result of transformation has wrong instance type " + result.getClass());
		} catch (JsonParseException e) {
			throw new RuntimeException("Unexpected JSON parse exception, check your transformer configuration", e);
		} catch (JsonMappingException e) {
			throw new RuntimeException("Unexpected JSON mapping exception, check your transformer configuration", e);
		} catch (IOException e) {
			throw new RuntimeException("Unexpected I/O exception, check your transformer configuration", e);
		}
	}
}
