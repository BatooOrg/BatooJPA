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
import javax.persistence.Basic;
import javax.persistence.Embeddable;

@Embeddable
@Access(AccessType.PROPERTY)
@SuppressWarnings("javadoc")
public class BookMark implements Serializable {

	private static final long serialVersionUID = 8027009758015834551L;

	private Long page;

	private String comment;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (this.getClass() != o.getClass())) {
			return false;
		}

		final BookMark bookMark = (BookMark) o;

		if (this.page != null ? !this.page.equals(bookMark.page) : bookMark.page != null) {
			return false;
		}

		return true;
	}

	@Basic
	public String getComment() {
		return this.comment;
	}

	@Basic
	public Long getPage() {
		return this.page;
	}

	@Override
	public int hashCode() {
		return this.page != null ? this.page.hashCode() : 0;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setPage(Long page) {
		this.page = page;
	}
}
