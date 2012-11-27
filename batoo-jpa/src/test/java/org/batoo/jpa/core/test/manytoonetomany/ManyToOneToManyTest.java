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
package org.batoo.jpa.core.test.manytoonetomany;

import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.jdbc.dbutils.QueryRunner;
import org.batoo.jpa.jdbc.dbutils.SingleValueHandler;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since 2.0.0
 */
public class ManyToOneToManyTest extends BaseCoreTest {

	private Person person() {
		final Person person = new Person("Ceylan");
		new Address(person, "Istanbul");
		new Address(person, "London");
		new Address(person, "New York");

		new Phone(person, "(111) 111-1111");
		new Phone(person, "(222) 222-2222");

		return person;
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)} person.
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testFind() {
		final Person person = this.person();
		this.persist(person);

		this.commit();
		this.close();

		final Person person2 = this.find(Person.class, person.getId());
		Assert.assertEquals(person.getName(), person2.getName());
		Assert.assertEquals(person.getAddresses().size(), person2.getAddresses().size());
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)} person.
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testFind2() {
		final Person person = this.person();
		this.persist(person);

		this.commit();
		this.close();

		final Phone phone = this.find(Phone.class, person.getPhones().get(0).getId());
		final Person person2 = phone.getPerson();
		Assert.assertEquals(person.getName(), person2.getName());
		Assert.assertEquals(person.getAddresses().size(), person2.getAddresses().size());
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)} person.
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testFind3() {
		final Person person = this.person();
		this.persist(person);

		this.commit();
		this.close();

		final Address address = this.find(Address.class, person.getAddresses().get(0).getId());
		final Person person2 = address.getPerson();
		Assert.assertEquals(person.getName(), person2.getName());
		Assert.assertEquals(person.getAddresses().size(), person2.getAddresses().size());
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)} person that is in the session.
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testFindInSession() {
		final Person person = this.person();
		this.persist(person);

		this.commit();

		final Person person2 = this.find(Person.class, person.getId());
		Assert.assertNotSame(person, person2);
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} address which does not cascade to Parent. PersistenceException expected.
	 * 
	 * 
	 * @since 2.0.0
	 */
	@Test(expected = PersistenceException.class)
	public void testPersistAddress() {
		this.persist(this.person().getAddresses().get(0));

		this.commit();
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} Parent which cascades to Child1.
	 * 
	 * @throws SQLException
	 *             ins case of an underlying SQL Exception
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testPersistPerson() throws SQLException {
		this.persist(this.person());

		this.commit();

		Assert.assertEquals(1,
			new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM Person", new SingleValueHandler<Number>()).intValue());

		Assert.assertEquals(3,
			new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM Address", new SingleValueHandler<Number>()).intValue());
	}
}
