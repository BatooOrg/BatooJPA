package org.batoo.jpa.community.test.t1;

import java.util.HashSet;

import org.batoo.jpa.community.test.BaseCoreTest;
import org.junit.Test;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class T1 extends BaseCoreTest {

	/**
	 * Ref: http://stackoverflow.com/questions/12755380/jpa-persisting-a-unidirectional-one-to-many-relationship-fails-with-eclipselin
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void test1() {
		this.begin();

		final Service service = new Service();
		service.setParameters(new HashSet<Parameter>());
		service.setName("test");
		final Parameter param = new Parameter();
		param.setName("test");
		service.getParameters().add(param);

		this.em().persist(service);
		this.em().flush();

		this.commit();
	}
}
