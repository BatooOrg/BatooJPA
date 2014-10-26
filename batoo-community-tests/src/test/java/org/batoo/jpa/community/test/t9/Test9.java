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

package org.batoo.jpa.community.test.t9;

import junit.framework.Assert;

import org.batoo.jpa.community.test.BaseCoreTest;
import org.junit.Test;

/**
 * 
 * @author barreiro
 */
@SuppressWarnings({ "serial", "javadoc" })
public class Test9 extends BaseCoreTest {

	@Test
	public void test9() {
		final Customer c = new Customer();
		c.setName("Test");
		this.persist(c);
		this.close();

		Assert.assertEquals(1, this.cq("select COUNT(c) from Customer c where c.deleted = FALSE").getResultList().size());
	}
}
