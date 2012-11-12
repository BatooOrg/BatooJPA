package org.batoo.jpa.community.test.t8;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.persistence.Query;

import org.batoo.jpa.community.test.BaseCoreTest;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.junit.Test;
import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;

/**
 * @author barreiro
 */
public class Test8 extends BaseCoreTest {

	private static final BLogger LOG = BLoggerFactory.getLogger(Test8.class);

	@Test
	public void test1() {

		final Customer c1 = new Customer("Luis", "Barreiro");
		final Customer c2 = new Customer("Luis", "Barabbas");

		this.begin();
		this.em().persist(c1);
		this.em().flush();
		this.commit();

		// Test scalar query, not named
		print(this.em().createQuery("select COUNT(a) from Customer a").getResultList());

		// Test named queries
		print(this.em().createNamedQuery(Customer.QUERY_ALL).getResultList());
		print(this.em().createNamedQuery(Customer.QUERY_LAST).setParameter("lastName", "Barreiro").getResultList());

		// Test named queries that return scalar results
		print(this.em().createNamedQuery(Customer.QUERY_FIRST).setParameter("lastName", "Barreiro").getResultList());
		print(this.em().createNamedQuery(Customer.QUERY_COUNT).getResultList());

		this.close();
	}

	private void print(List<?> result) {
		Test8.LOG.debug("### RESULT: {0}", Arrays.toString(result.toArray()));
	}

}
