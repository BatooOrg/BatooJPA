/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
 * @since $version
 */
public class CaseJpqlTest extends BaseCoreTest {

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testCase1() {
		this.persist(new Foo(1, "1", FooType.TYPE1));
		this.persist(new Foo(2, "2", FooType.TYPE2));
		this.persist(new Foo(3, "3", FooType.TYPE3));

		this.commit();
		this.close();

		Assert.assertEquals((Integer) 14, //
			this.cq("select sum(case\n" + //
				"    when f.number = 1 then 1 * f.number\n" + //
				"    when f.number = 2 then 2 * f.number\n" + //
				"    else 3 * f.number\n" + //
				"  end)\n" + //
				"from Foo f", Integer.class).getSingleResult());

		Assert.assertEquals((Integer) 14, //
			this.cq("select sum(case f.number\n" + //
				"    when 1 then 1 * f.number\n" + //
				"    when 2 then 2 * f.number\n" + //
				"    else 3 * f.number\n" + //
				"  end)\n" + //
				"from Foo f", Integer.class).getSingleResult());
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testCase2() {
		this.persist(new Foo(null, 1));
		this.persist(new Foo(5, 7));
		this.persist(new Foo(3, null));

		this.commit();
		this.close();

		Assert.assertEquals((Integer) 9, this.cq("select sum(coalesce(f.number, f.number2)) from Foo f", Integer.class).getSingleResult());

		Assert.assertEquals((Integer) 11, this.cq("select sum(coalesce(f.number2, f.number)) from Foo f", Integer.class).getSingleResult());
	}
}
