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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * 
 * @author asimarslan
 * @since $version
 */
@Entity
@TableGenerator(name = "order_id", allocationSize = 100)
@Table(name = "ORDER3")
@SuppressWarnings("javadoc")
public class Order3 {

	@Id
	@GeneratedValue(generator = "order_id", strategy = GenerationType.TABLE)
	private Long id;

	private Integer quantity;

	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	private Item3 item;

	public Order3() {
		super();
	}

	public Order3(Integer quantity, Item3 item) {
		super();
		this.quantity = quantity;
		this.item = item;

	}

	public Long getId() {
		return this.id;
	}

	public Item3 getItem() {
		return this.item;
	}

	public Integer getQuantity() {
		return this.quantity;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setItem(Item3 item) {
		this.item = item;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}
