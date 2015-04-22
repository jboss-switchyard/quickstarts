/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
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

package org.switchyard.transform.internal.json;

/**
 * @author Alejandro Montenegro &lt;<href="mailto:aamonten@gmail.com">aamonten@gmail.com</a>&gt;
 */
public class Person {

    private String name;
    private int age;

    public Person(){}
    /**
	 * @param string
	 * @param string2
	 */
	public Person(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public String getName() {
        return name;
    }

    public Person setName(String name) {
        this.name = name;
        return this;
    }

    public int getAge() {
        return age;
    }

    public Person setAge(int age) {
        this.age = age;
        return this;
    }
    
    public boolean equals(Object object){
    	
    	if(object instanceof Person){
    		Person person = (Person) object;
    		if( (person.getAge() == this.age ) && (person.getName().equals(this.name)) ){
    			return true;
    		}
    	}
    	return false;
    }
}
