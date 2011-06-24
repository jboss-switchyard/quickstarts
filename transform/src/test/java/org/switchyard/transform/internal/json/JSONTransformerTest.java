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

package org.switchyard.transform.internal.json;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.switchyard.config.model.ModelResource;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.internal.DefaultMessage;
import org.switchyard.internal.transform.BaseTransformerRegistry;
import org.switchyard.transform.Message2MessageTransformer;
import org.switchyard.transform.TransformSequence;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.TransformerRegistry;
import org.switchyard.transform.TransformerRegistryLoader;
import org.switchyard.transform.config.model.JSONTransformModel;
import org.switchyard.transform.internal.json.User.Gender;
import org.switchyard.transform.internal.json.User.Name;
import org.switchyard.transform.json.internal.JSON2JavaTransformer;
import org.switchyard.transform.json.internal.JSONTransformFactory;
import org.switchyard.transform.json.internal.Java2JSONTransformer;

/**
 * @author Alejandro Montenegro &lt;<href="mailto:aamonten@gmail.com">aamonten@gmail.com</a>&gt;
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class JSONTransformerTest {

	private final static String JSON_PERSON = "{\"age\":31,\"name\":\"Alejandro\"}";
	private final static String JSON_USER = "{\"verified\":false,\"gender\":\"MALE\",\"name\":{\"first\":\"Joe\",\"last\":\"Sixpack\"}}";
	private final static String JSON_USER_LIST = "[{\"name\":{\"first\":\"Joe\",\"last\":\"Sixpack\"},\"gender\":\"MALE\",\"verified\":false}]";
    private TransformerRegistry xformReg;

    public JSONTransformerTest() {
        xformReg = new BaseTransformerRegistry();
        new TransformerRegistryLoader(xformReg).loadOOTBTransforms();
    }

    @Test
	public void test_JSONResultPerson() {
		try {
			
			Transformer transformer = getTransformer("switchyard-config-01.xml");
			Object expected = toObject(JSON_PERSON, Person.class);
			Object result = toObject((String)transformer.transform(new Person("Alejandro", 31)), Person.class);
			assertEquals(expected, result);

		} catch (Exception e) {
			Assert.fail("caught an exception " + e.getMessage());
		}
	}
	
	@Test
	public void test_JAVAResultPerson() {
        try {
            DefaultMessage message = newMessage(JSON_PERSON);
            Transformer transformer = getTransformer("switchyard-config-02.xml");

            transformer.transform(message);
			assertEquals(new Person("Alejandro", 31), message.getContent());
		} catch (Exception e) {
			Assert.fail("caught an exception " + e.getMessage());
		}
	}

    private DefaultMessage newMessage(Object content) {
        DefaultMessage message = new DefaultMessage().setContent(content);
        message.setTransformerRegistry(xformReg);
        return message;
    }

    @Test
	public void test_JSONResultUser() {
		try {
			Transformer transformer = getTransformer("switchyard-config-03.xml");
			Object expected = toObject(JSON_USER,User.class);
			Object result = toObject((String)transformer.transform(getUser()), User.class);
			assertEquals(expected, result);
		} catch (Exception e) {
			Assert.fail("caught an exception " + e.getMessage());
		}
	}
	
	@Test
	public void test_JAVAResultUser() {
		try {
            DefaultMessage message = newMessage(JSON_USER);
			Transformer transformer = getTransformer("switchyard-config-04.xml");
            transformer.transform(message);
			assertEquals(getUser(), message.getContent());
		} catch (Exception e) {
			Assert.fail("caught an exception " + e.getMessage());
		}
	}
	
	@Test
	public void test_JAVAResultList() {
		try {
            DefaultMessage message = newMessage(JSON_USER_LIST);
			Transformer transformer = getTransformer("switchyard-config-05.xml");
            transformer.transform(message);
			assertEquals(toObject(JSON_USER_LIST, List.class), message.getContent());
		} catch (Exception e) {
			Assert.fail("caught an exception " + e.getMessage());
		}
	}
	
	@Test
	public void test_JAVAResultMap() {
		try {
            DefaultMessage message = newMessage(JSON_USER);
			Transformer transformer = getTransformer("switchyard-config-06.xml");
            transformer.transform(message);
			assertEquals(toObject(JSON_USER, Map.class), message.getContent());
		} catch (Exception e) {
			Assert.fail("caught an exception " + e.getMessage());
		}
	}
	
	@Test
	public void test_WrongFromInstance() {
		try {
			Transformer transformer = getTransformer("switchyard-config-03.xml");
			Object expected = toObject(JSON_USER,User.class);
			Object result = toObject((String)transformer.transform(new Person()), User.class);
			assertEquals(expected, result);
		} catch (Exception e) {
			assertEquals("The object to transform is of wrong instance type class org.switchyard.transform.internal.json.Person", e.getMessage());
		}
	}

	private Transformer getTransformer(String config) throws IOException {
		InputStream swConfigStream = getClass().getResourceAsStream(config);

		if (swConfigStream == null) {
			Assert.fail("null config stream.");
		}

		SwitchYardModel switchyardConfig = (SwitchYardModel) new ModelResource()
				.pull(swConfigStream);
		TransformsModel transforms = switchyardConfig.getTransforms();

		List<TransformModel> trans = transforms.getTransforms();
		JSONTransformModel jsonTransformModel = (JSONTransformModel) trans
				.get(0);

		if (jsonTransformModel == null) {
			Assert.fail("No json config.");
		}

		Transformer transformer = new JSONTransformFactory().newTransformer(jsonTransformModel);

		if (!(transformer instanceof JSON2JavaTransformer ||transformer instanceof Java2JSONTransformer)) {
			Assert.fail("Not an instance of a JSONTransformer.");
		}

		return transformer;
	}
	
	private User getUser(){
		User user = new User();
		Name name = new Name();
		name.setFirst("Joe");
		name.setLast("Sixpack");
		user.setName(name);
		user.setVerified(false);
		user.setGender(Gender.MALE);
		return user;
	}
	
	private Object toObject(String jsonString, Class clazz) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(jsonString, clazz);
	}
}
