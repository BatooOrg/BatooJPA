/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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

package org.batoo.jpa.core.test.ddl;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since 2.0.0
 */
public class DdlTest extends BaseCoreTest {

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected boolean lazySetup() {
		return true;
	}

	/**
	 * Tests the ddl drop mode.
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testDdlTEST0_Drop() {
		this.setupEmf().close();
		this.setupEmf().close();
	}

	/**
	 * Tests the ddl create mode.
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testDdlTEST1_Create() {
		this.setupEmf("create").close();
	}

	/**
	 * Tests the ddl none mode.
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testDdlTEST2_None() {
		this.setupEmf("none").close();
	}

	/**
	 * Tests the ddl drop mode.
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testDdlTEST3_Update() {
		this.setupEmf("update1").close();
		this.setupEmf("update2").close();
	}

}
