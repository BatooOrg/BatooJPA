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

package org.batoo.jpa.core.test.dublicatetable2;

import com.google.common.collect.Lists;

import javax.persistence.*;
import java.util.List;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
public class Foo {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Integer id;

	private String value;

    @ManyToMany(targetEntity = Bar.class, fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    @JoinTable(name = "fooBar",
            joinColumns = {@JoinColumn(name = "fooId", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "barId", referencedColumnName = "id")})
    private List<FooBarEntity> fooBars = Lists.newArrayList();

	/**
	 * Returns the id of the Foo1.
	 *
	 * @return the id of the Foo1
	 *
	 * @since 2.0.0
	 */
	public Integer getId() {
		return this.id;
	}



	/**
	 * Returns the value of the Foo1.
	 *
	 * @return the value of the Foo1
	 *
	 * @since 2.0.0
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Sets the value of the Foo1.
	 *
	 * @param value
	 *            the value to set for Foo1
	 *
	 * @since 2.0.0
	 */
	public void setValue(String value) {
		this.value = value;
	}

    public List<FooBarEntity> getFooBars() {
        return fooBars;
    }

    public void setFooBars(List<FooBarEntity> fooBars) {
        this.fooBars = fooBars;
    }
}
