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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "library_")
@Access(AccessType.PROPERTY)
@SuppressWarnings("javadoc")
public class Library implements Serializable {

	private static final long serialVersionUID = 6360420736014459567L;

	private Long identity;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (this.getClass() != o.getClass())) {
			return false;
		}

		final Library library = (Library) o;

		if (this.identity != null ? !this.identity.equals(library.identity) : library.identity != null) {
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
