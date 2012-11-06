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
package org.batoo.jpa.core.test.accesstype;

import javax.persistence.metamodel.Metamodel;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class AccessTypeTest extends BaseCoreTest {

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AccessTypeTest() {
		super();
	}

	/**
	 * 
	 * Tests deployment when then there is no explicit access type set.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testAccessType() {
		final Metamodel mm = this.em().getMetamodel();

		Assert.assertEquals(6, mm.getManagedTypes().size());
	}
}
