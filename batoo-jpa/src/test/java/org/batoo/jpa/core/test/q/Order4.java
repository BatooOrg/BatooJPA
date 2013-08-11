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

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FetchType;
import javax.persistence.FieldResult;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * 
 * @author tolgagokmen
 * @since 2.0.1
 */
@SqlResultSetMappings({ @SqlResultSetMapping(name = "OrderItemResultsDisc4", entities = { //
	@EntityResult(entityClass = Order4.class,//
		discriminatorColumn = "discol",//
		fields = {//
		@FieldResult(name = "id", column = "order_id"),//
			@FieldResult(name = "quantity", column = "order_quantity"),//
			@FieldResult(name = "item", column = "order_item") }),//
		@EntityResult(entityClass = Item4.class) }),//

})
@Entity
@TableGenerator(name = "order_id", allocationSize = 100)
@Table(name = "ORDER4")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DISC", discriminatorType = DiscriminatorType.STRING, length = 20)
@DiscriminatorValue("ORDER")
@SuppressWarnings("javadoc")
public class Order4 extends OrderBase {

	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	protected Item4 item;

	public Order4() {
		super();
	}

	public Order4(Integer quantity, Item4 item) {
		super();
		this.quantity = quantity;
		this.item = item;

	}

	public Item4 getItem() {
		return this.item;
	}

	public void setItem(Item4 item) {
		this.item = item;
	}

}
