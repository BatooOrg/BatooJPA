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
package org.batoo.jpa.community.test.t6;

import org.batoo.jpa.community.test.BaseCoreTest;
import org.junit.Test;

/**
 * Test for the Methods defined in interfaces using generics are wrongly evaluated for their actual java / sql-type.
 * https://github.com/BatooOrg/BatooJPA/issues/78
 * 
 * @author hceylan
 * @since $version
 */
public class Test6 extends BaseCoreTest {

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Test6() {
		super();
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testInterface() {
	}
}
