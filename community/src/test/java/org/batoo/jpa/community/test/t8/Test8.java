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
package org.batoo.jpa.community.test.t8;

import java.util.Arrays;
import java.util.List;

import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.jpa.community.test.BaseCoreTest;
import org.junit.Test;

/**
 *
 * @author barreiro
 */
@SuppressWarnings({ "serial", "javadoc" })
public class Test8 extends BaseCoreTest {

	private static final BLogger LOG = BLoggerFactory.getLogger(Test8.class);

	private void print(List<?> result) {
		Test8.LOG.debug("### RESULT: {0}", Arrays.toString(result.toArray()));
	}

	@Test
	public void test1() {
		final Customer c1 = new Customer("Luis", "Barreiro");

		this.persist(c1);
		this.commit();

		// Test scalar query, not named
		this.print(this.em().createQuery("select COUNT(a) from Customer a").getResultList());

		// Test named queries
		this.print(this.em().createNamedQuery(Customer.QUERY_ALL).getResultList());
		this.print(this.em().createNamedQuery(Customer.QUERY_LAST).setParameter("lastName", "Barreiro").getResultList());

		// Test named queries that return scalar results
		this.print(this.em().createNamedQuery(Customer.QUERY_FIRST).setParameter("lastName", "Barreiro").getResultList());
		this.print(this.em().createNamedQuery(Customer.QUERY_COUNT).getResultList());

		this.close();
	}

	@Test
	public void test2() {
		final Customer cr = new Customer("John", "Barabbas");
		this.persist(cr);

		final Customer c2 = new ValuedCustomer("Luis", "Barreiro", 1000);
		c2.setId(1337);
		this.persist(c2);

		this.commit();
		this.close();

		// Test named queries
		this.print(this.em().createNamedQuery(Customer.QUERY_ALL).getResultList());
		this.print(this.em().createNamedQuery(ValuedCustomer.QUERY_CREDIT_TOTAL).getResultList());

		// Lets add a reference and increase the credit
		this.begin();

		ValuedCustomer vc = this.find(ValuedCustomer.class, 1337);
		vc.setCredit(2000);
		vc.getReferences().add(cr);
		this.commit();

		this.close();
	}
}
