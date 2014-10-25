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

package org.batoo.jpa.community.test.i103;

import java.sql.SQLException;
import java.util.Date;

import org.batoo.jpa.community.test.BaseCoreTest;
import org.batoo.jpa.community.test.NoDatasource;
import org.junit.Test;

/**
 * Test for https://github.com/BatooOrg/BatooJPA/issues/103#issuecomment-10717359
 * 
 * @author hceylan
 */
@SuppressWarnings({ "serial", "javadoc" })
public class TestIssue103 extends BaseCoreTest {

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void teardown() throws SQLException {
	}

	@Test
	@NoDatasource
	public void testIssue103() {
		Users user = new Users("hceylan", "passw0rd");
		user.setCreatedDt(new Date());
		this.persist(user);
		this.commit();

		this.close();

		user = this.find(Users.class, user.getUserName());
		user.setLastLoginDt(new Date());
		user.setLoggedInViaMobile('N');
		user.setLastLogoutDt(null);
		user.setPassword("password");
		user.setPasswordLastChangedDt(null);
		user.setActiveStatusChangedDt(null);
		user.setActive('Y');
		user.setTheme("aristo");

		this.begin();
		this.commit();
	}
}
