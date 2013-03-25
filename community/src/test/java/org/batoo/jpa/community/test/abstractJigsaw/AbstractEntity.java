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
package org.batoo.jpa.community.test.abstractJigsaw;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "CONCRETE_TYPE", discriminatorType = DiscriminatorType.STRING, length = 50)
@Table(name = "AbstractEntity")
@SuppressWarnings("javadoc")
public abstract class AbstractEntity extends TopMappedSuperClass {
	public abstract static class AbstractEntityUnwrapper implements TopMappedSuperClass.IUnwrapper {
		@Override
		public abstract AbstractEntity unwrap();
	}

	@Basic
	@Column(nullable = false)
	private String myAbstractProperty;

	public AbstractEntity() {
		super();
	}

	@Override
	public Long getId() {
		return this.id;
	}

	public String getMyAbstractProperty() {
		return this.myAbstractProperty;
	}

	abstract public String getMyConcreteProperty();

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public void setMyAbstractProperty(String myAbstractProperty) {
		this.myAbstractProperty = myAbstractProperty;
	}

	@Override
	public abstract AbstractEntityUnwrapper unwrapper();
}
