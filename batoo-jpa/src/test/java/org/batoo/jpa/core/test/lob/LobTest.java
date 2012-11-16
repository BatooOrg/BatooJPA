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
package org.batoo.jpa.core.test.lob;

import java.util.Arrays;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

import com.google.common.collect.Sets;

/**
 * @author hceylan
 * 
 * @since 2.0.0
 */
public class LobTest extends BaseCoreTest {

	private static final String BLOB_DATA = "Blob Data";
	private static final String CLOB_DATA = "Clob Data";
	private static final String VALUE1 = "Value1";
	private static final String VALUE2 = "Value2";

	/**
	 * Tests to {@link EntityManager#persist(Object)} then {@link EntityManager#find(Class, Object)} with lob values
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testLob() {
		final Foo foo = new Foo();
		foo.getValues().add(LobTest.VALUE1);
		foo.getValues().add(LobTest.VALUE2);

		foo.setClob(LobTest.CLOB_DATA);
		foo.setBlob(LobTest.BLOB_DATA.getBytes());

		this.persist(foo);

		this.commit();

		this.close();

		final Foo foo2 = this.find(Foo.class, foo.getKey());
		Assert.assertEquals(foo.getKey(), foo2.getKey());
		Assert.assertEquals(Sets.newHashSet(LobTest.VALUE1, LobTest.VALUE2), foo2.getValues());
		Assert.assertEquals(Arrays.toString(LobTest.BLOB_DATA.getBytes()), Arrays.toString(foo2.getBlob()));
		Assert.assertEquals(LobTest.CLOB_DATA, foo2.getClob());
	}
}
