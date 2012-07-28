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
package org.batoo.jpa.core.test.q.jpql.inheritence;

import javax.persistence.TypedQuery;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.q.Contractor;
import org.batoo.jpa.core.test.q.Employee;
import org.batoo.jpa.core.test.q.Exempt;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class InheritenceJpqlTest extends BaseCoreTest {

	/**
	 * @since $version
	 * @author hceylan
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testType() {
		this.persist(new Employee());
		this.persist(new Exempt());
		this.persist(new Exempt());
		this.persist(new Contractor());
		this.commit();
		this.close();

		TypedQuery<Employee> q;
		TypedQuery<Class> q2;

		q = this.cq("select e from Employee e where type(e) = Exempt", Employee.class);
		Assert.assertEquals(2, q.getResultList().size());

		q = this.cq("select object(e) from Employee e where type(e) = :p", Employee.class).setParameter("p", Exempt.class);
		Assert.assertEquals(2, q.getResultList().size());

		q2 = this.cq("select type(e) from Employee e where type(e) = :p", Class.class).setParameter("p", Exempt.class);
		Assert.assertEquals(Exempt.class, q2.getResultList().get(0));

		q2 = this.cq("select type(e.id) from Employee e where type(e) = :p", Class.class).setParameter("p", Exempt.class);
		Assert.assertEquals(Integer.class, q2.getResultList().get(0));
	}
}
