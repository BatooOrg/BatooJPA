package org.batoo.jpa.community.test.t3;

import javax.persistence.TypedQuery;

import org.batoo.jpa.community.test.BaseCoreTest;
import org.junit.Test;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class T3 extends BaseCoreTest {

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void test() {
		final TypedQuery<E1> q = this.cq("select e1 from E1 e1 where  e1.e2.e3 = :oem", E1.class);

		q.setParameter("oem", new E3());
		q.getResultList();
	}
}
