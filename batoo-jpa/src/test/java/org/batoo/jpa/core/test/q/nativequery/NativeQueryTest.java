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
import org.batoo.jpa.core.test.q.Item3Pk;
import org.batoo.jpa.core.test.q.Item4;
import org.batoo.jpa.core.test.q.Order;
import org.batoo.jpa.core.test.q.Order2;
import org.batoo.jpa.core.test.q.Order3;
import org.batoo.jpa.core.test.q.Order4;
import org.junit.Assert;
import org.junit.Test;

/**
 * Native Query Test Cases
 * 
 * @author asimarslan
 * @since version
 */
public class NativeQueryTest extends BaseCoreTest {

	/**
	 * test for column mapping
	 * 
	 * @since $version
	 */
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

	/**
	 * Test for DiscriminatorValue
	 * 
	 * @since $version
	 */
	@Test
	public void testDiscriminatorValue() {
		final Item4 i4 = new Item4("item4", "the item 4.");

		final Order4 o1 = new Order4(20, i4);

		this.persist(o1);

		this.commit();
		this.close();

		final Query q = this.em().createNativeQuery("SELECT o.id AS order_id, " //
			+ "o.quantity AS order_quantity, "//
			+ "o.item_id AS order_item, "//
			+ "o.DISC as discol,"//
			+ "i.id, i.name, i.description "//
			+ "FROM ORDER4 o, Item4 i "//
			+ "WHERE (o.quantity > 5) AND (o.item_id = i.id)", "OrderItemResultsDisc4");

		final List<?> resultList = q.getResultList();

		Assert.assertEquals(1, q.getResultList().size());

		for (final Object oArr : resultList) {
			final Object[] row = (Object[]) oArr;

			Assert.assertTrue(row[0] instanceof Order4);
			final Order4 order = (Order4) row[0];
			Assert.assertTrue(this.em().contains(order));

			Assert.assertTrue(row[1] instanceof Item4);
			final Item4 item = (Item4) row[1];
			Assert.assertTrue(this.em().contains(item));

			Assert.assertEquals(1, item.getOrders().size());

			Assert.assertEquals(item, order.getItem());

		}

	}

	/**
	 * test for embeddedId attribute in native query
	 * 
	 * @since $version
	 */
	@Test
	public void testEmbeddedIdNativeQuery() {
		final Item3 i1 = new Item3(new Item3Pk(1l, "item1"), "the item 1.");

		final Order3 o1 = new Order3(30, i1);

		this.persist(o1);

		this.commit();
		this.close();

		final Query q = this.em().createNativeQuery(//
			"SELECT o.id AS order_id, "//
				+ "o.quantity AS order_quantity, " //
				+ "o.item_id AS order_item_id, " //
				+ "o.item_name AS order_item_name, "//
				+ "i.id, i.name, i.description " //
				+ "FROM ORDER3 o, Item3 i "//
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
			Assert.assertEquals(1, order.getItem().getOrders().size());

			Assert.assertTrue(row[1] instanceof Item3);
			final Item3 item = (Item3) row[1];
			Assert.assertTrue(this.em().contains(item));

			Assert.assertEquals("item1", item.getItemPk().getName());
			Assert.assertEquals("the item 1.", item.getDescription());
			//
			Assert.assertEquals(1, item.getOrders().size());
			//

		}
	}

	/**
	 * test for attribute with IdClass in native query
	 * 
	 * @since $version
	 */
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
				+ "FROM ORDER2 o, Item2 i "//
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
			Assert.assertEquals(1, item.getOrders().size());
			//

		}

	}

	/**
	 * test Simple named native query
	 * 
	 * @since $version
	 */
	@Test
	public void testNamedNativeQuery() {
		final Item i1 = new Item("item1", "the item 1.");

		final Order o1 = new Order(30, i1);

		this.persist(o1);

		this.commit();
		this.close();

		final Query q = this.em().createNamedQuery("namedNativeQuery1").setParameter(1, 5);

		Assert.assertEquals(1, q.getResultList().size());

	}

	/**
	 * Simple single entity named native query test
	 * 
	 * @since $version
	 */
	@Test
	public void testNamedNativeQuery2() {
		final Item i1 = new Item("item1", "the item 1.");

		final Order o1 = new Order(20, i1);

		this.persist(o1);

		this.commit();
		this.close();

		final Query q = this.em().createNamedQuery("namedNativeQuery2").setParameter(1, 5);

		Assert.assertEquals(1, q.getResultList().size());

	}

	/**
	 * test Simple named native query in orm.xml
	 * 
	 * @since $version
	 */
	@Test
	public void testNamedNativeQuery3() {
		final Item i1 = new Item("item1", "the item 1.");

		final Order o1 = new Order(30, i1);

		this.persist(o1);

		this.commit();
		this.close();

		final Query q = this.em().createNamedQuery("namedNativeQuery3").setParameter(1, 5);

		Assert.assertEquals(1, q.getResultList().size());

	}

	/**
	 * Simple Multi entity native query test
	 * 
	 * @since $version
	 */
	@Test
	public void testSimpleMultipleSelect() {
		final Item _item = new Item("item1", "the item 1.");

		final Order _order = new Order(30, _item);

		this.persist(_order);

		this.commit();
		this.close();

		final Query q = this.em().createNativeQuery("SELECT o.id AS order_id, " //
			+ "49 AS order_quantity, "//
			+ "o.item_id AS order_item, "//
			+ "i.id, 'itemX' as name, i.description "//
			+ "FROM ORDER_T o, Item i "//
			+ "WHERE (o.quantity > 5) AND (o.item_id = i.id)", "OrderItemResults2");

		final List<?> resultList = q.getResultList();

		Assert.assertEquals(1, q.getResultList().size());

		for (final Object oArr : resultList) {
			final Object[] row = (Object[]) oArr;

			Assert.assertTrue(row[0] instanceof Order);
			final Order order = (Order) row[0];
			Assert.assertTrue(this.em().contains(order));

			Assert.assertEquals(49, order.getQuantity().intValue());
			Assert.assertEquals("itemX", order.getItem().getName());
			Assert.assertEquals("the item 1.", order.getItem().getDescription());
			Assert.assertEquals(1, order.getItem().getOrders().size());

			Assert.assertTrue(row[1] instanceof Item);
			final Item item = (Item) row[1];
			Assert.assertTrue(this.em().contains(item));

			Assert.assertEquals("itemX", item.getName());
			Assert.assertEquals("the item 1.", item.getDescription());

			Assert.assertEquals(1, item.getOrders().size());
		}

	}

	/**
	 * test Simple Multi entity native query
	 * 
	 * @since $version
	 */
	@Test
	public void testSimpleMultiSelectWithParameters() {
		final Item i1 = new Item("item1", "the item 1.");

		final Order o1 = new Order(30, i1);

		this.persist(o1);

		this.commit();
		this.close();

		final Query q = this.em().createNativeQuery(//
			"SELECT o.id, " //
				+ "o.quantity, "//
				+ "o.item_id, "//
				+ "i.id, 'itemX' as name, i.description " //
				+ "FROM ORDER_T o, Item i "//
				+ "WHERE (o.quantity > ?) AND (o.item_id = i.id)", "OrderItemResults").setParameter(1, 5);

		final List<?> resultList = q.getResultList();

		Assert.assertEquals(1, q.getResultList().size());

		for (final Object oArr : resultList) {

			final Object[] row = (Object[]) oArr;

			Assert.assertTrue(row[0] instanceof Order);
			final Order order = (Order) row[0];
			Assert.assertTrue(this.em().contains(order));

			Assert.assertEquals(30, order.getQuantity().intValue());
			Assert.assertEquals("itemX", order.getItem().getName());
			Assert.assertEquals("the item 1.", order.getItem().getDescription());
			Assert.assertEquals(1, order.getItem().getOrders().size());

			Assert.assertTrue(row[1] instanceof Item);
			final Item item = (Item) row[1];
			Assert.assertTrue(this.em().contains(item));

			Assert.assertEquals("itemX", item.getName());
			Assert.assertEquals("the item 1.", item.getDescription());

			Assert.assertEquals(1, item.getOrders().size());

		}

	}

	/**
	 * Simple single entity native query test
	 * 
	 * @since $version
	 */
	@Test(expected = javax.persistence.PersistenceException.class)
	public void testSingleEntityWithParameters() {
		final Item i1 = new Item("item1", "the item 1.");

		final Order o1 = new Order(20, i1);

		this.persist(o1);

		this.commit();
		this.close();

		final Query q = this.em().createNativeQuery(//
			"SELECT o.id, " //
				+ "49 as quantity, "//
				+ "o.item_id, "//
				+ "i.id, 'itemX' as name, i.description " //
				+ "FROM ORDER_T o, Item i "//
				+ "WHERE (o.quantity > ? OR o.quantity > ?) AND (o.item_id = i.id)", Order.class)//
		// .setParameter(1, 5)//
		.setParameter(2, 5);

		final List<?> resultList = q.getResultList();

		Assert.assertEquals(1, q.getResultList().size());

		for (final Object row : resultList) {

			Assert.assertTrue(row instanceof Order);
			final Order order = (Order) row;
			Assert.assertTrue(this.em().contains(order));

			Assert.assertEquals(49, order.getQuantity().intValue());
			Assert.assertEquals("item1", order.getItem().getName());
			Assert.assertEquals("the item 1.", order.getItem().getDescription());
		}

	}

}
