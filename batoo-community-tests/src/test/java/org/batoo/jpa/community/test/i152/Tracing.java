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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author fma
 */
@Entity
@Table(name = "TRACING")
@SuppressWarnings("javadoc")
public class Tracing implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@Basic(optional = false)
	@GeneratedValue
	@Column(name = "ID")
	private Integer id;

	@Column(name = "REFERENCE_1")
	private String reference1;

	public Tracing() {
	}

	public Tracing(Integer id) {
		this.id = id;
	}

	//
	// @Override
	// public boolean equals(Object object) {
	// // TODO: Warning - this method won't work in the case the id fields are not set
	// if (!(object instanceof Tracing)) {
	// return false;
	// }
	// final Tracing other = (Tracing) object;
	// if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
	// return false;
	// }
	// return true;
	// }

	public Integer getId() {
		return this.id;
	}

	public String getReference1() {
		return this.reference1;
	}

	// @Override
	// public int hashCode() {
	// int hash = 0;
	// hash += (this.id != null ? this.id.hashCode() : 0);
	// return hash;
	// }

	public void setReference1(String reference1) {
		this.reference1 = reference1;
	}

}
