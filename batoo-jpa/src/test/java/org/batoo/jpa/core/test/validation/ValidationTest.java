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
package org.batoo.jpa.core.test.validation;

import javax.validation.ConstraintViolationException;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class ValidationTest extends BaseCoreTest {

	/**
	 * Tests the validation persist
	 * 
	 * @since $version
	 */
	@Test(expected = ConstraintViolationException.class)
	public void testPersist() {
		final Foo foo = new Foo();

		foo.getBars().add(new Bar());

		this.persist(foo);
		this.commit();
	}

	/**
	 * Tests the validation persist
	 * 
	 * @since $version
	 */
	@Test(expected = ConstraintViolationException.class)
	public void testRemove() {
		Foo foo = new Foo();
		foo.setValue("value");
		this.persist(foo);

		this.commit();
		this.close();
		this.begin();

		foo = this.find(Foo.class, foo.getId());
		foo.setValue2(10);

		this.commit();
		this.close();
		this.begin();

		foo = this.find(Foo.class, foo.getId());

		this.remove(foo);

		this.commit();
	}

	/**
	 * Tests the validation persist
	 * 
	 * @since $version
	 */
	@Test
	public void testRemoveOk() {
		Foo foo = new Foo();
		foo.setValue("value");

		final Bar bar = new Bar();
		bar.setFoo(foo);
		bar.setValue("value");
		foo.getBars().add(bar);

		this.persist(foo);
		this.commit();

		this.close();

		this.begin();

		foo = this.find(Foo.class, foo.getId());
		this.remove(foo);

		this.commit();
	}

	/**
	 * Tests the validation persist
	 * 
	 * @since $version
	 */
	@Test(expected = ConstraintViolationException.class)
	public void testUpdate() {
		Foo foo = new Foo();
		foo.setValue("value");

		final Bar bar = new Bar();
		bar.setFoo(foo);
		bar.setValue("value");
		foo.getBars().add(bar);

		this.persist(foo);
		this.commit();

		this.close();

		this.begin();

		foo = this.find(Foo.class, foo.getId());
		foo.setValue("value_");

		this.commit();
	}

	/**
	 * Tests the validation persist
	 * 
	 * @since $version
	 */
	@Test
	public void testUpdateOk() {
		Foo foo = new Foo();
		foo.setValue("value");

		final Bar bar = new Bar();
		bar.setFoo(foo);
		bar.setValue("value");
		foo.getBars().add(bar);

		this.persist(foo);
		this.commit();

		this.close();

		this.begin();

		foo = this.find(Foo.class, foo.getId());
		foo.setValue("value_");
		foo.setValue2(1);
		this.commit();
	}
}
