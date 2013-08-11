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

package org.batoo.jpa.core.test.q;

import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 
 * @author asimarslan
 * @since 2.0.1
 */
@Entity
@Table(name = "Item3")
@SuppressWarnings("javadoc")
public class Item3 {

	@EmbeddedId
	private Item3Pk itemPk;

	private String description;

	@OneToMany(mappedBy = "item", fetch = FetchType.EAGER)
	public List<Order3> orders;

	public Item3() {
		super();
	}

	public Item3(Item3Pk itemPk, String description) {
		super();
		this.setItemPk(itemPk);
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	public Item3Pk getItemPk() {
		return this.itemPk;
	}

	public List<Order3> getOrders() {
		return this.orders;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setItemPk(Item3Pk itemPk) {
		this.itemPk = itemPk;
	}

	public void setOrders(List<Order3> orders) {
		this.orders = orders;
	}

}
