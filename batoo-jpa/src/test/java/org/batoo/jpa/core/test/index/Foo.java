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
package org.batoo.jpa.core.test.index;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.batoo.jpa.annotations.Index;
import org.batoo.jpa.annotations.Indexes;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
@Indexes({ @Index(name = "IX_VALUE2", columns = "value2"), //
	@Index(name = "IX_VALUE1_2", columns = { "value", "value2" }) })
public class Foo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Index(name = "IX_VALUE")
	private String value;

	private String value2;

	/**
	 * Returns the id of the Foo1.
	 * 
	 * @return the id of the Foo1
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the value of the Foo1.
	 * 
	 * @return the value of the Foo1
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Returns the value2 of the Foo.
	 * 
	 * @return the value2 of the Foo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getValue2() {
		return this.value2;
	}

	/**
	 * Sets the value of the Foo1.
	 * 
	 * @param value
	 *            the value to set for Foo1
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Sets the value2 of the Foo.
	 * 
	 * @param value2
	 *            the value2 to set for Foo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setValue2(String value2) {
		this.value2 = value2;
	}
}
