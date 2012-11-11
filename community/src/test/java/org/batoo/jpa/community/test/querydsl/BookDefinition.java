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
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.OrderColumn;

@Embeddable
@Access(AccessType.PROPERTY)
@SuppressWarnings("javadoc")
public class BookDefinition implements Serializable {

	private static final long serialVersionUID = 3570098308959717614L;

	private String name;

	private String description;

	@ElementCollection
	private List<BookMark> bookMarks;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (this.getClass() != o.getClass())) {
			return false;
		}

		final BookDefinition that = (BookDefinition) o;

		if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
			return false;
		}

		return true;
	}

	@ElementCollection()
	@CollectionTable(name = "book_bookmarks")
	@OrderColumn()
	public List<BookMark> getBookMarks() {
		return this.bookMarks;
	}

	@Basic
	public String getDescription() {
		return this.description;
	}

	@Basic
	public String getName() {
		return this.name;
	}

	@Override
	public int hashCode() {
		return this.name != null ? this.name.hashCode() : 0;
	}

	public void setBookMarks(List<BookMark> bookMarks) {
		this.bookMarks = bookMarks;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}
}
