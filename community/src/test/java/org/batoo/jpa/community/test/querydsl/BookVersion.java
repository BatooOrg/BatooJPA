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
