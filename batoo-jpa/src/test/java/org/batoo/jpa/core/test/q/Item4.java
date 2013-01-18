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
package org.batoo.jpa.core.test.q;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * 
 * @author tolgagokmen
 * @since $version
 */
@Entity
@TableGenerator(name = "item_id", allocationSize = 100)
@Table(name = "Item4")
@SuppressWarnings("javadoc")
public class Item4 {

	@Id
	@GeneratedValue(generator = "item_id", strategy = GenerationType.TABLE)
	private Long id;

	private String name;

	private String description;

	@OneToMany(mappedBy = "item", fetch = FetchType.EAGER)
	public List<Order4> orders;

	public Item4() {
		super();
	}

	public Item4(String name, String description) {
		super();
		this.name = name;
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	public Long getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public List<Order4> getOrders() {
		return this.orders;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOrders(List<Order4> orders) {
		this.orders = orders;
	}

}
