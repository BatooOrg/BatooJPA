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
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
@DiscriminatorValue("ConcreteEntity")
@SuppressWarnings("javadoc")
public class ConcreteEntity extends AbstractEntity {

	public static class Unwrapper extends AbstractEntity.AbstractEntityUnwrapper {
		private final ConcreteEntity _bo;

		public Unwrapper(ConcreteEntity bo) {
			this._bo = bo;
		}

		@Override
		public ConcreteEntity unwrap() {
			return this._bo;
		}
	}

	@Basic
	@Column(nullable = false)
	private String myConcreteProperty;

	public ConcreteEntity() {
		super();
	}

	public ConcreteEntity(String myAbstractProperty, String myConcreteProperty) {
		setMyAbstractProperty(myAbstractProperty);
		this.myConcreteProperty = myConcreteProperty;
	}

	@Override
	public String getMyConcreteProperty() {
		return this.myConcreteProperty;
	}

	@Override
	public Unwrapper unwrapper() {
		return new Unwrapper(this);
	}

}
