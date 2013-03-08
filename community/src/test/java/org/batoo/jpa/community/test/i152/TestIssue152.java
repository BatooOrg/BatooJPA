package org.batoo.jpa.community.test.i152;

import junit.framework.Assert;

import org.batoo.jpa.community.test.BaseCoreTest;
import org.batoo.jpa.community.test.NoDatasource;
import org.junit.Test;

/**
 * test for issue 152
 * 
 * @author asimarslan
 * @since $version
 */
public class TestIssue152 extends BaseCoreTest {

	@Test
	@NoDatasource
	public void test() {
		final Kid kid = new Kid();
		kid.setDescription("john the kid");

		this.persist(kid);

		this.commit();
		this.close();

		this.begin();

		final Mother mother = new Mother();

		final Kid kidTheSame = new Kid(kid.getId());
		mother.setKid(kidTheSame);

		this.persist(mother);

		this.commit();
		this.close();

		final Kid kid2 = this.find(Kid.class, kid.getId());

		Assert.assertEquals(kid.getDescription(), kid2.getDescription());

	}
}
