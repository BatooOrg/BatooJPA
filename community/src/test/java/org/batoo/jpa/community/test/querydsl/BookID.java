/*
 * Copyright 2011, Mysema Ltd
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
package org.batoo.jpa.community.test.querydsl;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bookid_")
@Access(AccessType.PROPERTY)
@SuppressWarnings("javadoc")
public class BookID implements Serializable {

	private static final long serialVersionUID = -3205025118656391776L;

	private Long identity;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (this.getClass() != o.getClass())) {
			return false;
		}

		final BookID bookID = (BookID) o;

		if (this.identity != null ? !this.identity.equals(bookID.identity) : bookID.identity != null) {
			return false;
		}

		return true;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getIdentity() {
		return this.identity;
	}

	@Override
	public int hashCode() {
		return this.identity != null ? this.identity.hashCode() : 0;
	}

	public void setIdentity(Long identity) {
		this.identity = identity;
	}
}
