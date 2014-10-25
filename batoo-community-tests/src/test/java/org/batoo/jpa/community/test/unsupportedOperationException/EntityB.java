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

package org.batoo.jpa.community.test.unsupportedOperationException;

import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
@SuppressWarnings("javadoc")
public class EntityB {
	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private final Long id = null;

	@OneToMany(
		fetch = FetchType.LAZY,
		cascade = { CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE },
		targetEntity = EntityA.class,
		mappedBy = "entityB")
	private Set<EntityA> entitiesA;

	@Basic
	@Column(name = "CODE", nullable = false)
	private String code;

	public String getCode() {
		return this.code;
	}

	public Set<EntityA> getEntitiesA() {
		return this.entitiesA;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setEntitiesA(Set<EntityA> entitiesA) {
		this.entitiesA = entitiesA;
	}

	public Long getId() {
		return id;
	}
}
