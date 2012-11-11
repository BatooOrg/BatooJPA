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
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name = "bookversion_")
@Access(AccessType.PROPERTY)
@SuppressWarnings("javadoc")
public class BookVersion implements Serializable {

	private static final long serialVersionUID = -1697470794339057030L;

	private BookID bookID;

	private BookVersionPK pk = new BookVersionPK();

	private Library library;

	private BookDefinition definition;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (this.getClass() != o.getClass())) {
			return false;
		}

		final BookVersion that = (BookVersion) o;

		if (this.bookID != null ? !this.bookID.equals(that.bookID) : that.bookID != null) {
			return false;
		}
		if (this.library != null ? !this.library.equals(that.library) : that.library != null) {
			return false;
		}

		return true;
	}

	@MapsId("bookID")
	@ManyToOne(cascade = CascadeType.ALL)
	public BookID getBookID() {
		return this.bookID;
	}

	@Embedded
	public BookDefinition getDefinition() {
		return this.definition;
	}

	@MapsId("library")
	@ManyToOne(cascade = CascadeType.ALL)
	public Library getLibrary() {
		return this.library;
	}

	@EmbeddedId
	public BookVersionPK getPk() {
		return this.pk;
	}

	@Override
	public int hashCode() {
		int result = this.bookID != null ? this.bookID.hashCode() : 0;
		result = (31 * result) + (this.library != null ? this.library.hashCode() : 0);
		return result;
	}

	public void setBookID(BookID bookID) {
		this.bookID = bookID;
	}

	public void setDefinition(BookDefinition definition) {
		this.definition = definition;
	}

	public void setLibrary(Library library) {
		this.library = library;
	}

	public void setPk(BookVersionPK pk) {
		this.pk = pk;
	}
}
