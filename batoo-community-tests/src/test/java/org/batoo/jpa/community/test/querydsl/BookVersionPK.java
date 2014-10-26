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
