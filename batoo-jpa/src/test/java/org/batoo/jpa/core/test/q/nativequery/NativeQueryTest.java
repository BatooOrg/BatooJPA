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
package org.batoo.jpa.core.test.q.nativequery;

import java.util.List;

import javax.persistence.Query;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.q.Item;
import org.batoo.jpa.core.test.q.Item2;
import org.batoo.jpa.core.test.q.Item3;
import org.batoo.jpa.core.test.q.Order;
import org.batoo.jpa.core.test.q.Order2;
import org.batoo.jpa.core.test.q.Order3;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * 
 * @author asimarslan
 * @since 2.0.1
 */
public class NativeQueryTest extends BaseCoreTest {

	@Test
	public void testColumnMappingNativeQuery() {
		final Item i1 = new Item("item1", "the item 1.");

		final Order o1 = new Order(30, i1);

		this.persist(o1);

		this.commit();
		this.close();

		final Query q = this.em().createNativeQuery("SELECT o.id AS order_id, " //
			+ "o.quantity AS order_quantity, "//
			+ "o.item_id AS order_item, "//
			+ "i.name AS item_name "//
			+ "FROM ORDER_T o, Item i "//
			+ "WHERE (o.quantity > 25) AND (o.item_id = i.id)", "OrderResults3");

		final List<?> resultList = q.getResultList();
		Assert.assertEquals(1, resultList.size());

		for (final Object oArr : resultList) {
			final Object[] row = (Object[]) oArr;

			Assert.assertTrue(row[0] instanceof Order);
			final Order order = (Order) row[0];
			Assert.assertTrue(this.em().contains(order));

			Assert.assertEquals(30, order.getQuantity().intValue());
			Assert.assertEquals("item1", order.getItem().getName());
			Assert.assertEquals("the item 1.", order.getItem().getDescription());

			Assert.assertTrue(row[1] instanceof String);
			final String item = (String) row[1];

			Assert.assertEquals("item1", item);

		}

	}

	@Test
	@Ignore
	public void testDiscriminatorValue() {
		Assert.fail();
	}

	@Test
	public void testEmbeddedIdNativeQuery() {
		final Item2 i1 = new Item2(1l, "item1", "the item 1.");

		final Order2 o1 = new Order2(30, i1);

		this.persist(o1);

		this.commit();
		this.close();

		final Query q = this.em().createNativeQuery(//
			"SELECT o.id AS order_id, "//
				+ "o.quantity AS order_quantity, " //
				+ "o.item_id AS order_item_id, " //
				+ "o.item_name AS order_item_name, "//
				+ "i.id, i.name, i.description " //
				+ "FROM Order2 o, Item2 i "//
				+ "WHERE (o.quantity > 25) AND (o.item_id = i.id) AND (o.item_name = i.name)", "OrderItemEmbeddedIdResults");

		final List<?> resultList = q.getResultList();
		Assert.assertEquals(1, resultList.size());

		for (final Object oArr : resultList) {
			final Object[] row = (Object[]) oArr;

			Assert.assertTrue(row[0] instanceof Order3);
			final Order3 order = (Order3) row[0];
			Assert.assertTrue(this.em().contains(order));

			Assert.assertEquals(30, order.getQuantity().intValue());
			Assert.assertEquals("item1", order.getItem().getItemPk().getName());
			Assert.assertEquals("the item 1.", order.getItem().getDescription());
			// Assert.assertEquals(1, order.getItem().getOrders().size());

			Assert.assertTrue(row[1] instanceof Item3);
			final Item3 item = (Item3) row[1];
			Assert.assertTrue(this.em().contains(item));

			Assert.assertEquals("item1", item.getItemPk().getName());
			Assert.assertEquals("the item 1.", item.getDescription());
			//
			// Assert.assertEquals(1, item.getOrders().size());
			//
			// Assert.assertEquals(item.getId(), order.getItem().getId());

		}
	}

	@Test
	public void testIdClassNativeQuery() {
		final Item2 i1 = new Item2(1l, "item1", "the item 1.");

		final Order2 o1 = new Order2(30, i1);

		this.persist(o1);

		this.commit();
		this.close();

		final Query q = this.em().createNativeQuery(//
			"SELECT o.id AS order_id, "//
				+ "o.quantity AS order_quantity, " //
				+ "o.item_id AS order_item_id, " //
				+ "o.item_name AS order_item_name, "//
				+ "i.id, i.name, i.description " //
				+ "FROM Order2 o, Item2 i "//
				+ "WHERE (o.quantity > 25) AND (o.item_id = i.id) AND (o.item_name = i.name)", "OrderItemResultsIdClass");

		final List<?> resultList = q.getResultList();
		Assert.assertEquals(1, resultList.size());

		for (final Object oArr : resultList) {
			final Object[] row = (Object[]) oArr;

			Assert.assertTrue(row[0] instanceof Order2);
			final Order2 order = (Order2) row[0];
			Assert.assertTrue(this.em().contains(order));

			Assert.assertEquals(30, order.getQuantity().intValue());
			Assert.assertEquals("item1", order.getItem().getName());
			Assert.assertEquals("the item 1.", order.getItem().getDescription());
			Assert.assertEquals(1, order.getItem().getOrders().size());

			Assert.assertTrue(row[1] instanceof Item2);
			final Item2 item = (Item2) row[1];
			Assert.assertTrue(this.em().contains(item));

			Assert.assertEquals("item1", item.getName());
			Assert.assertEquals("the item 1.", item.getDescription());
			//
			// Assert.assertEquals(1, item.getOrders().size());
			//

		}

	}

	@Test
	public void testMultiRowSingleSelectWithParameters() {
		final Item i1 = new Item("item1", "the item 1.");

		final Order o1 = new Order(20, i1);

		this.persist(o1);

		this.commit();
		this.close();

		final Query q = this.em().createNativeQuery("SELECT o.id, " //
			+ "o.quantity, "//
			+ "o.item_id "//
			+ "FROM ORDER_T o, Item i "//
			+ "WHERE o.quantity > ?", Order.class).setParameter(0, 5);

		final List resultList = q.getResultList();

		Assert.assertEquals(1, q.getResultList().size());

		for (final Object row : resultList) {

			Assert.assertTrue(row instanceof Order);
			final Order order = (Order) row;
			Assert.assertTrue(this.em().contains(order));

			Assert.assertEquals(20, order.getQuantity().intValue());
			Assert.assertEquals("item1", order.getItem().getName());
			Assert.assertEquals("the item 1.", order.getItem().getDescription());
		}

	}

	@Test
	public void testSimpleMultipleSelect() {
		final Item i1 = new Item("item1", "the item 1.");
		final Item i2 = new Item("item2", "the item 2.");

		final Order o1 = new Order(20, i1);
		final Order o2 = new Order(30, i2);

		// this.persist(i1);
		// this.persist(i2);
		this.persist(o1);
		this.persist(o2);

		this.commit();
		this.close();

		final Query q = this.em().createNativeQuery("SELECT o.id AS order_id, " //
			+ "o.quantity AS order_quantity, "//
			+ "o.item_id AS order_item, "//
			+ "i.id, i.name, i.description "//
			+ "FROM ORDER_T o, Item i "//
			+ "WHERE (o.quantity > 5) AND (o.item_id = i.id)", "OrderItemResults2");

		final List resultList = q.getResultList();

		Assert.assertEquals(2, q.getResultList().size());

		for (final Object oArr : resultList) {
			final Object[] row = (Object[]) oArr;

			Assert.assertTrue(row[0] instanceof Order);
			final Order order = (Order) row[0];
			Assert.assertTrue(this.em().contains(order));

			// Assert.assertEquals(30, order.getQuantity().intValue());
			// Assert.assertEquals("item2", order.getItem().getName());
			// Assert.assertEquals("the item 2.", order.getItem().getDescription());

			Assert.assertTrue(row[1] instanceof Item);
			final Item item = (Item) row[1];
			Assert.assertTrue(this.em().contains(item));

			// Assert.assertEquals("item2", item.getName());
			// Assert.assertEquals("the item 2.", item.getDescription());

			Assert.assertEquals(1, item.getOrders().size());

			Assert.assertEquals(item.getId(), order.getItem().getId());

		}

	}

	@Test
	public void testSimpleSingleSelectWithParameters() {
		final Item i1 = new Item("item1", "the item 1.");

		final Order o1 = new Order(20, i1);

		this.persist(o1);

		this.commit();
		this.close();

		final Query q = this.em().createNativeQuery("SELECT o.id, " //
			+ "o.quantity, "//
			+ "o.item_id "//
			+ "FROM ORDER_T o, Item i "//
			+ "WHERE (o.quantity > ?) AND (o.item_id = i.id)", Order.class).setParameter(0, 5);

		final List resultList = q.getResultList();

		Assert.assertEquals(1, q.getResultList().size());

		for (final Object row : resultList) {

			Assert.assertTrue(row instanceof Order);
			final Order order = (Order) row;
			Assert.assertTrue(this.em().contains(order));

			Assert.assertEquals(20, order.getQuantity().intValue());
			Assert.assertEquals("item1", order.getItem().getName());
			Assert.assertEquals("the item 1.", order.getItem().getDescription());
		}

	}

}
