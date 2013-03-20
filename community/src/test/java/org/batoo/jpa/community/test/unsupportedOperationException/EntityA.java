/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
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
package org.batoo.jpa.community.test.unsupportedOperationException;

import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;

@Entity
@SuppressWarnings("javadoc")
public class EntityA {
	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private final Long id = null;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = EntityB.class)
	@JoinColumn(name = "ID_ENTITYB", referencedColumnName = "ID")
	private EntityB entityB;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "ENTITYA_NAME", joinColumns = @JoinColumn(name = "ID"))
	@MapKeyEnumerated(EnumType.STRING)
	@MapKeyColumn(name = "LOCALE", nullable = false)
	@Column(name = "TRANSLATION", nullable = false)
	private Map<FieldLocale, String> name;

	public EntityB getEntityB() {
		return this.entityB;
	}

	public Map<FieldLocale, String> getName() {
		return this.name;
	}

	public void setEntityB(EntityB entityB) {
		this.entityB = entityB;
	}

	public void setName(Map<FieldLocale, String> name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}
}
