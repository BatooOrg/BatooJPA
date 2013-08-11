/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
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
