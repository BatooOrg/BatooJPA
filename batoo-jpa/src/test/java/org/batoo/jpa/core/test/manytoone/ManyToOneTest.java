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
package org.batoo.jpa.core.test.manytoone;

import java.lang.reflect.Field;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.sql.DataSource;

import junit.framework.Assert;

import org.batoo.jpa.core.impl.jdbc.dbutils.QueryRunner;
import org.batoo.jpa.core.impl.jdbc.dbutils.SingleValueHandler;
import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
@Ignore
// TODO https://github.com/BatooOrg/BatooJPA/issues/77
public class ManyToOneTest extends BaseCoreTest {

	private Person person() {
		final Person person = new Person("Ceylan");

		final Address address = new Address("Istanbul");
		person.setHomeAddress(address);

		return person;
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)} person.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testColumnInsertUpdateFalse() {
		final Person person = this.person();

		this.persist(person);

		this.commit();
		this.close();

		final Person person2 = this.find(Person.class, person.getId());

		Assert.assertEquals(person.getName(), person2.getName());
		Assert.assertNotNull(person2.getAddressId());
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)} person.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testFind() {
		final Person person = this.person();
		this.persist(person);

		this.commit();
		this.close();

		final Person person2 = this.find(Person.class, person.getId());
		Assert.assertEquals(person.getName(), person2.getName());
		Assert.assertEquals(person2.getAddressId(), person2.getHomeAddress().getId());
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)} person that is in the session.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testFindInSession() {
		final Person person = this.person();
		this.persist(person);

		this.commit();

		final Person person2 = this.find(Person.class, person.getId());
		Assert.assertSame(person, person2);
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)} person.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testJoinColumn() {
		final Person person = this.person();
		this.persist(person);

		this.commit();
		this.close();

		final Person person2 = this.find(Person.class, person.getId());

		Assert.assertEquals(person.getName(), person2.getName());
		Assert.assertNotNull(person2.getAddressId());
		Assert.assertEquals(person2.getAddressId(), person2.getHomeAddress().getId());
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} Parent which cascades to Child1.
	 * 
	 * @throws SQLException
	 *             ins case of an underlying SQL Exception
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testJoinColumnName() throws SQLException {
		Field f = null;
		try {
			f = Person.class.getDeclaredField("homeAddress");

			final JoinColumn annotation = f.getAnnotation(JoinColumn.class);
			Assert.assertEquals("address_id", annotation.name());

		}
		catch (final NoSuchFieldException e) {
			e.printStackTrace();
			Assert.fail();
		}
		catch (final SecurityException e) {
			e.printStackTrace();
			Assert.fail();
		}

		this.persist(this.person());

		this.commit();
		Assert.assertNotNull(new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT address_id FROM Person", new SingleValueHandler<Number>()));
	}
}
