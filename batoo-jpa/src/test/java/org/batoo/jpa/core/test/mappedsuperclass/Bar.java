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

import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import com.google.common.collect.Lists;

/**
 * 
 * @author hceylan
 * @since $version
 */
@MappedSuperclass
public class Bar {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Integer key;

	private String barValue;

	@OneToMany(cascade = CascadeType.ALL)
	private final List<Quux> quuxes = Lists.newArrayList();

	/**
	 * Returns the barValue.
	 * 
	 * @return the barValue
	 * @since $version
	 */
	public String getBarValue() {
		return this.barValue;
	}

	/**
	 * Returns the key.
	 * 
	 * @return the key
	 * @since $version
	 */
	public Integer getKey() {
		return this.key;
	}

	/**
	 * Returns the quuxes.
	 * 
	 * @return the quuxes
	 * @since $version
	 */
	public List<Quux> getQuuxes() {
		return this.quuxes;
	}

	/**
	 * Sets the barValue.
	 * 
	 * @param barValue
	 *            the barValue to set
	 * @since $version
	 */
	public void setBarValue(String barValue) {
		this.barValue = barValue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "Bar [key=" + this.key + "]";
	}

}
