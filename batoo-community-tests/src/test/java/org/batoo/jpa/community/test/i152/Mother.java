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

package org.batoo.jpa.community.test.i152;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("javadoc")
@Entity
@Table(name = "MOTHER")
public class Mother implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@Basic(optional = false)
	@GeneratedValue
	@Column(name = "ID")
	private Integer id;

	@Column(name = "REFERENCE")
	private String reference;

	@JoinColumn(name = "KID_ID", referencedColumnName = "ID")
	@ManyToOne(fetch = FetchType.LAZY, cascade = {})
	// @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
	private Kid kid;

	public Mother() {
	}

	public Mother(Integer id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Mother)) {
			return false;
		}
		final Mother other = (Mother) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	public Integer getId() {
		return this.id;
	}

	public Kid getKid() {
		return this.kid;
	}

	public String getReference() {
		return this.reference;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (this.id != null ? this.id.hashCode() : 0);
		return hash;
	}

	public void setKid(Kid kid) {
		this.kid = kid;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

}
