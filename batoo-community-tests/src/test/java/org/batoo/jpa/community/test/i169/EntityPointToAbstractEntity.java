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

package org.batoo.jpa.community.test.i169;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table
@SuppressWarnings("javadoc")
public class EntityPointToAbstractEntity {
	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id = null;

	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = AbstractEntity.class)
	@JoinColumn(name = "ID_ABSTRACT_ENTITY", referencedColumnName = "ID", nullable = false)
	private AbstractEntity abstractEntity;

	public EntityPointToAbstractEntity() {
		super();
	}

	public AbstractEntity getAbstractEntity() {
		return this.abstractEntity;
	}

	public Long getId() {
		return this.id;
	}

	public void setAbstractEntity(AbstractEntity abstractEntity) {
		this.abstractEntity = abstractEntity;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
