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

package org.batoo.jpa.community.test.serializationTestModel;

import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table
@SuppressWarnings("javadoc")
public class ConcreteEntity extends AbstractEntity {

	@Basic
	@Column(nullable = false)
	private String code;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, targetEntity = EntityB.class, mappedBy = "concreteEntity")
	private Set<EntityB> entitiesB;

	public ConcreteEntity() {
		super();
	}

	public String getCode() {
		return this.code;
	}

	@Override
	public String getFoo() {
		return "Foo";
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public void setEntitiesB(Set<EntityB> entitiesB) {
		this.entitiesB = entitiesB;
	}
	
	public Set<EntityB> getEntitiesB() {
		return entitiesB;
	}
}
