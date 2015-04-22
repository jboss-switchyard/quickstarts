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

import org.mvel2.util.ThisLiteral;

/**
 * @author Alejandro Montenegro &lt;<href="mailto:aamonten@gmail.com">aamonten@gmail.com</a>&gt;
 */
public class User {
	public enum Gender {
		MALE, FEMALE
	};

	public static class Name {
		private String _first, _last;

		public String getFirst() {
			return _first;
		}

		public String getLast() {
			return _last;
		}

		public void setFirst(String s) {
			_first = s;
		}

		public void setLast(String s) {
			_last = s;
		}
		
		public boolean equals(Object o){
			if(o instanceof Name){
				Name name = (Name)o;
				if(name.getFirst().equals(_first) && name.getLast().equals(_last))
					return true;
			}
			return false;
		}
	}

	private Gender _gender;
	private Name _name;
	private boolean _isVerified;

	public Name getName() {
		return _name;
	}

	public boolean isVerified() {
		return _isVerified;
	}

	public Gender getGender() {
		return _gender;
	}

	public void setName(Name n) {
		_name = n;
	}

	public void setVerified(boolean b) {
		_isVerified = b;
	}

	public void setGender(Gender g) {
		_gender = g;
	}
	
	public boolean equals(Object o){
		
		if(o instanceof User){
			User user = (User)o;
			
			if(user.getGender().equals(this._gender) && (user._isVerified == this._isVerified) && user.getName().equals(this._name))
				return true;
		}
		return false;
	}
}
