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

package org.batoo.jpa.community.test.t4;

import javax.persistence.TypedQuery;

import org.batoo.jpa.community.test.BaseCoreTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * Test case for https://github.com/BatooOrg/BatooJPA/issues/70
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class Test4 extends BaseCoreTest {

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void test() {
		E2 e2 = new E2("Value");
		E1 e1 = new E1(e2);

		this.persist(e1);
		this.persist(e2);

		this.commit();
		this.close();

		e2 = this.find(E2.class, e2.getId());
		this.close();

		final TypedQuery<E1> q = this.cq("select e1 from E1 e1 where e1.e2 = :e2", E1.class);

		q.setParameter("e2", e2);
		e1 = q.getSingleResult();

		Assert.assertEquals(e1.getE2().getId(), e2.getId());
		Assert.assertEquals(e1.getE2().getValue(), e2.getValue());
	}
}
