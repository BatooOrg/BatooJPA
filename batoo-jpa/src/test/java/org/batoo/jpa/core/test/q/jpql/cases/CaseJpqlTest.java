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
package org.batoo.jpa.core.test.q.jpql.cases;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.q.Foo;
import org.batoo.jpa.core.test.q.Foo.FooType;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class CaseJpqlTest extends BaseCoreTest {

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testCase1() {
		this.persist(new Foo(1, "1", FooType.TYPE1));
		this.persist(new Foo(2, "2", FooType.TYPE2));
		this.persist(new Foo(3, "3", FooType.TYPE3));

		this.commit();
		this.close();

		Assert.assertEquals(14, //
			this.cq("select sum(case\n" + //
				"    when f.number = 1 then 1 * f.number\n" + //
				"    when f.number = 2 then 2 * f.number\n" + //
				"    else 3 * f.number\n" + //
				"  end)\n" + //
				"from Foo f", Number.class).getSingleResult().intValue());

		Assert.assertEquals(14, //
			this.cq("select sum(case f.number\n" + //
				"    when 1 then 1 * f.number\n" + //
				"    when 2 then 2 * f.number\n" + //
				"    else 3 * f.number\n" + //
				"  end)\n" + //
				"from Foo f", Number.class).getSingleResult().intValue());
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testCase2() {
		this.persist(new Foo(null, 1));
		this.persist(new Foo(5, 7));
		this.persist(new Foo(3, null));

		this.commit();
		this.close();

		Assert.assertEquals(9, this.cq("select sum(coalesce(f.number, f.number2)) from Foo f", Number.class).getSingleResult().intValue());

		Assert.assertEquals(11, this.cq("select sum(coalesce(f.number2, f.number)) from Foo f", Number.class).getSingleResult().intValue());
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testNullif() {
		this.persist(new Foo(2, 1));
		this.persist(new Foo(5, 7));
		this.persist(new Foo(null, 2));

		this.commit();
		this.close();

		Assert.assertEquals(5, this.cq("select sum(nullif(f.number, 2)) from Foo f", Number.class).getSingleResult().intValue());

		Assert.assertEquals(8, this.cq("select sum(nullif(coalesce(f.number2, f.number), 2)) from Foo f", Number.class).getSingleResult().intValue());

		Assert.assertEquals(10, this.cq("select sum(nullif(coalesce(f.number2, f.number), 5)) from Foo f", Number.class).getSingleResult().intValue());

		Assert.assertEquals(10, this.cq("select sum(nullif(coalesce(f.number2, f.number), 99)) from Foo f", Number.class).getSingleResult().intValue());
	}
}
