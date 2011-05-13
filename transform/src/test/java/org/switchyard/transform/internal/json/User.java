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
