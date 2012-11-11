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

import javax.persistence.Embeddable;

@Embeddable
@SuppressWarnings("javadoc")
public class BookVersionPK implements Serializable {

	private static final long serialVersionUID = 8483495681236266676L;

	private Long bookID;

	private Long library;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (this.getClass() != o.getClass())) {
			return false;
		}

		final BookVersionPK that = (BookVersionPK) o;

		if (this.bookID != null ? !this.bookID.equals(that.bookID) : that.bookID != null) {
			return false;
		}
		if (this.library != null ? !this.library.equals(that.library) : that.library != null) {
			return false;
		}

		return true;
	}

	public Long getBookID() {
		return this.bookID;
	}

	public Long getLibrary() {
		return this.library;
	}

	@Override
	public int hashCode() {
		int result = this.bookID != null ? this.bookID.hashCode() : 0;
		result = (31 * result) + (this.library != null ? this.library.hashCode() : 0);
		return result;
	}

	public void setBookID(Long bookID) {
		this.bookID = bookID;
	}

	public void setLibrary(Long library) {
		this.library = library;
	}
}
