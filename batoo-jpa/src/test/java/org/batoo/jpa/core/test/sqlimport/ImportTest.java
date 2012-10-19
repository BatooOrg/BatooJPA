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
package org.batoo.jpa.core.test.sqlimport;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class ImportTest extends BaseCoreTest {

	/**
	 * Tests the index creation.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testIndex() {
		Assert.assertEquals(3, this.em().createQuery("select count(distinct f) from Foo f", Long.class).getSingleResult().intValue());

		Assert.assertEquals(6, this.em().createQuery("select sum(f.id) from Foo f", Long.class).getSingleResult().intValue());
	}
}
