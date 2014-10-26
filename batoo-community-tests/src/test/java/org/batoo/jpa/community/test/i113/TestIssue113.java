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

package org.batoo.jpa.community.test.i113;

import java.util.Date;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.batoo.jpa.community.test.BaseCoreTest;
import org.junit.Test;

/**
 * Test for https://github.com/BatooOrg/BatooJPA/issues/113
 * 
 * @author hceylan
 */
@SuppressWarnings("javadoc")
public class TestIssue113 extends BaseCoreTest {

	@Test(expected = PersistenceException.class)
	public void testIssue113() {
		final Foo foo = new Foo();

		foo.setId(1);
		foo.setLastActivity(new Date());

		this.persist(foo);
		this.commit();
		this.close();

		final Query query = this.em().createQuery("UPDATE Foo t SET t.lastActivity = ?1 WHERE t.id = ?2");
		query.setParameter(1, new Date());
		query.setParameter(2, 1);
		query.executeUpdate();

	}
}
