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
package org.batoo.jpa.core.test.priotizer;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class PriotizerTest extends BaseCoreTest {

	private Parent parent() {
		final Parent parent = new Parent();

		parent.getChildren1().add(new Child(parent));
		parent.getChildren1().add(new Child(parent));

		parent.getChildren2().add(new Child());
		parent.getChildren2().add(new Child());

		return parent;
	}

	/**
	 * 
	 * @since $version
	 */
	@Test
	public void testPersist() {
		final Parent parent = this.parent();
		this.persist(parent);

		this.commit();
	}

	/**
	 * @since $version
	 */
	@Test
	public void testPersistAndRemove() {
		final Parent parent = this.parent();
		this.persist(parent);

		this.commit();

		this.persist(this.parent());
		this.remove(parent);

		this.commit();
	}
}
