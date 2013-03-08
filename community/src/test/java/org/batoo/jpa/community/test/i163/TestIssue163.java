package org.batoo.jpa.community.test.i163;

import static org.junit.Assert.assertNotNull;

import org.batoo.jpa.community.test.BaseCoreTest;
import org.batoo.jpa.community.test.NoDatasource;
import org.junit.Test;

public class TestIssue163 extends BaseCoreTest {
	@Test
	@NoDatasource
	public void test(){
		assertNotNull("The staticmetamodel isn't populated", User_.login);
	}
}
