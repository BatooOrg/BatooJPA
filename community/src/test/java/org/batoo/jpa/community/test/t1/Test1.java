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
package org.batoo.jpa.community.test.t1;

import java.util.HashSet;

import org.batoo.jpa.community.test.BaseCoreTest;
import org.junit.Test;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class Test1 extends BaseCoreTest {

	/**
	 * Ref: http://stackoverflow.com/questions/12755380/jpa-persisting-a-unidirectional-one-to-many-relationship-fails-with-eclipselin
	 * 
	 * @since 2.0.0
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
