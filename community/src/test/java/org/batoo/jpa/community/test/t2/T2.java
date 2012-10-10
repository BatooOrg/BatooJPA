package org.batoo.jpa.community.test.t2;

import org.batoo.jpa.community.test.BaseCoreTest;
import org.junit.Test;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class T2 extends BaseCoreTest {

	/**
	 * Ref: http://stackoverflow.com/questions/12795407/jpa-how-to-select-objects-wich-has-no-multiple-attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void test1() {
		this.cq("select ent from OpSubject_vr ent where ent.okved_id_mult is empty");
	}

	/**
	 * Ref: http://stackoverflow.com/questions/12795407/jpa-how-to-select-objects-wich-has-no-multiple-attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test(expected = IllegalArgumentException.class)
	public void test2() {
		this.begin();

		this.cq("select ent from OpSubject_vr ent where ent.okved_id_mult is null");
	}

	/**
	 * Ref: http://stackoverflow.com/questions/12795407/jpa-how-to-select-objects-wich-has-no-multiple-attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void test3() {
		this.cq("select distinct ent from OpSubject_vr ent left join ent.okved_id_mult i1 where i1.code is null");
	}
}
