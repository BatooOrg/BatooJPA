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

package org.batoo.jpa.core.test.enhance.abstractclass;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 
 * @author ylemoigne
 * @since 2.0.1
 */
@Entity
@Table
@SuppressWarnings("javadoc")
public class ConcreteEntity extends AbstractEntity {

	@Basic
	@Column(nullable = false)
	private String code;

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
}
