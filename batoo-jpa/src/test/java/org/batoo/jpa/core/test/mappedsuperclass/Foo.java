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
package org.batoo.jpa.core.test.mappedsuperclass;

import java.util.List;

import javax.persistence.AssociationOverride;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import com.google.common.collect.Lists;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
@AssociationOverride(joinTable = @JoinTable(name = "Bar_Quux"), name = "quuxes")
public class Foo extends Bar {

	private String fooValue;

	@OneToMany(cascade = CascadeType.ALL)
	private final List<Quux> fooQuuxes = Lists.newArrayList();

	/**
	 * Returns the fooQuuxes.
	 * 
	 * @return the fooQuuxes
	 * @since $version
	 */
	public List<Quux> getFooQuuxes() {
		return this.fooQuuxes;
	}

	/**
	 * Returns the fooValue.
	 * 
	 * @return the fooValue
	 * @since $version
	 */
	public String getFooValue() {
		return this.fooValue;
	}

	/**
	 * Sets the fooValue.
	 * 
	 * @param fooValue
	 *            the fooValue to set
	 * @since $version
	 */
	public void setFooValue(String fooValue) {
		this.fooValue = fooValue;
	}
}
