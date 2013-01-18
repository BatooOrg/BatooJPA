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
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FetchType;
import javax.persistence.FieldResult;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * 
 * @author asimarslan
 * @since $version
 */
@SqlResultSetMappings({ @SqlResultSetMapping(name = "WidgetOrderResults", entities = @EntityResult(entityClass = Order.class)),//

	@SqlResultSetMapping(name = "OrderItemResults", entities = { @EntityResult(entityClass = Order.class), @EntityResult(entityClass = Item.class) }),//
	@SqlResultSetMapping(name = "ItemResults", entities = @EntityResult(entityClass = Item.class)),//

	@SqlResultSetMapping(name = "OrderItemResults2", entities = { //
		@EntityResult(entityClass = Order.class, fields = {//
			@FieldResult(name = "id", column = "order_id"),//
				@FieldResult(name = "quantity", column = "order_quantity"),//
				@FieldResult(name = "item", column = "order_item") }),//
			@EntityResult(entityClass = Item.class) }),//

	@SqlResultSetMapping(name = "OrderResults3", entities = { //
		@EntityResult(entityClass = Order.class, fields = { //
			@FieldResult(name = "id", column = "order_id"), //
				@FieldResult(name = "quantity", column = "order_quantity"),//
				@FieldResult(name = "item", column = "order_item") }) }, //
		columns = { @ColumnResult(name = "item_name") }),//

	@SqlResultSetMapping(name = "OrderItemResults4", entities = { @EntityResult(entityClass = Order.class, fields = { //
		@FieldResult(name = "id", column = "order_id"),//
			@FieldResult(name = "quantity", column = "order_quantity"), //
			@FieldResult(name = "item.id", column = "order_item_id"),//
			@FieldResult(name = "item.name", column = "order_item_name") //
		}),//
		@EntityResult(entityClass = Item.class) //
		}),//

	@SqlResultSetMapping(name = "OrderItemResultsIdClass", entities = { //
		@EntityResult(entityClass = Order2.class, fields = {//
			@FieldResult(name = "id", column = "order_id"),//
				@FieldResult(name = "quantity", column = "order_quantity"),//
				@FieldResult(name = "item.id", column = "order_item_id"),//
				@FieldResult(name = "item.name", column = "order_item_name") }),//
			@EntityResult(entityClass = Item2.class) }),

	@SqlResultSetMapping(name = "OrderItemEmbeddedIdResults", entities = {//
		@EntityResult(entityClass = Order3.class, fields = { //
			@FieldResult(name = "id", column = "order_id"), //
				@FieldResult(name = "quantity", column = "order_quantity"),//
				@FieldResult(name = "item.itemPk.id", column = "order_item_id"),//
				@FieldResult(name = "item.itemPk.name", column = "order_item_name") }), //
			@EntityResult(entityClass = Item3.class) })

})
@Entity
@TableGenerator(name = "order_id", allocationSize = 100)
@Table(name = "ORDER_T")
@SuppressWarnings("javadoc")
public class Order {

	@Id
	@GeneratedValue(generator = "order_id", strategy = GenerationType.TABLE)
	private Long id;

	private Integer quantity;

	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	private Item item;

	public Order() {
		super();
	}

	public Order(Integer quantity, Item item) {
		super();
		this.quantity = quantity;
		this.item = item;

	}

	public Long getId() {
		return this.id;
	}

	public Item getItem() {
		return this.item;
	}

	public Integer getQuantity() {
		return this.quantity;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}
