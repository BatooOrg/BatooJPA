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

package org.batoo.jpa.community.test.t2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "SPI_V_OP_OBJ_SUBJECT_VR")
@SuppressWarnings("javadoc")
public class OpSubject_vr implements Serializable {

	/** id */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", nullable = false)
	private Long id;

	/** name */
	@Column(name = "F_NAME")
	private String f_name;

	/** type */
	@Column(name = "SUBJECT_TYPE")
	private String subject_type;

	/** okved */
	@ManyToMany
	@JoinTable(
		name = "SPI_V_OP_OBJ_SUBJECT_VR_OKVED",
		joinColumns = { @JoinColumn(name = "OBJECT_ID", referencedColumnName = "ID", nullable = false) },
		inverseJoinColumns = { @JoinColumn(name = "SPR_ID", referencedColumnName = "ID", nullable = false) })
	private final List<OpSprOkved> okved_id_mult = new ArrayList<OpSprOkved>();

	public String getF_name() {
		return this.f_name;
	}

	public Long getId() {
		return this.id;
	}

	public List<OpSprOkved> getOkved_id_mult() {
		return this.okved_id_mult;
	}

	public String getSubject_type() {
		return this.subject_type;
	}

	public void setF_name(String f_name) {
		this.f_name = f_name;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setSubject_type(String subject_type) {
		this.subject_type = subject_type;
	}
}
