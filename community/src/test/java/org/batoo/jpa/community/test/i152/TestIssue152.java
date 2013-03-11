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
package org.batoo.jpa.community.test.i152;

import junit.framework.Assert;

import org.batoo.jpa.community.test.BaseCoreTest;
import org.batoo.jpa.community.test.NoDatasource;
import org.junit.Test;

/**
 * test for issue 152
 * 
 * @author asimarslan
 * @since $version
 */
@SuppressWarnings("javadoc")
public class TestIssue152 extends BaseCoreTest {

	@Test
	@NoDatasource
	public void test() {
		final Kid kid = new Kid();
		kid.setDescription("john the kid");

		this.persist(kid);

		this.commit();
		this.close();

		this.begin();

		final Mother mother = new Mother();

		final Kid kidTheSame = new Kid(kid.getId());
		mother.setKid(kidTheSame);

		this.persist(mother);

		this.commit();
		this.close();

		final Kid kid2 = this.find(Kid.class, kid.getId());

		Assert.assertEquals(kid.getDescription(), kid2.getDescription());

	}
}
