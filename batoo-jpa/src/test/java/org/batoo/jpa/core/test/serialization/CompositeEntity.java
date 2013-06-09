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
package org.batoo.jpa.core.test.serialization;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 
 * @author ylemoigne
 * @since 2.0.1
 */
@Entity
@Table
@SuppressWarnings("javadoc")
public class CompositeEntity extends TopMappedSuperClass {
	private static final long serialVersionUID = 1L;

	@Basic
	@Column(nullable = false)
	private String code;

	@OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = CompositeEntity.class)
	@JoinTable(name = "COMPOSITEENTITY_NEXTCOMPOSITE", joinColumns = @JoinColumn(name = "ID_COMPOSITE"), inverseJoinColumns = @JoinColumn(
		name = "ID_PREVIOUS_COMPOSITE"), uniqueConstraints = @UniqueConstraint(columnNames = { "COMPOSITEENTITY_NEXTCOMPOSITE", "ID_PREVIOUS_COMPOSITE" }))
	private CompositeEntity nextComposite;

	private transient ConcreteEntity contreteEntity;

	public CompositeEntity() {
		super();
	}

	public String getCode() {
		return this.code;
	}

	public ConcreteEntity getContreteEntity() {
		return this.contreteEntity;
	}

	public CompositeEntity getNextComposite() {
		return this.nextComposite;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setContreteEntity(ConcreteEntity contreteEntity) {
		this.contreteEntity = contreteEntity;
	}

	public void setNextComposite(CompositeEntity nextComposite) {
		this.nextComposite = nextComposite;
	}
}
