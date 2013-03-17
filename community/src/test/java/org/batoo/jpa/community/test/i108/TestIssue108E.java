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
package org.batoo.jpa.community.test.i108;

import java.util.Properties;

import javax.ejb.EJB;

import junit.framework.Assert;

import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.StatefulBean;
import org.apache.openejb.jee.jpa.unit.PersistenceUnit;
import org.apache.openejb.junit.ApplicationComposer;
import org.apache.openejb.junit.Configuration;
import org.apache.openejb.junit.Module;
import org.batoo.jpa.core.BatooPersistenceProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * The extended version of the test
 * 
 * @author hceylan
 * @since 2.0.1
 */
@RunWith(ApplicationComposer.class)
@SuppressWarnings("javadoc")
public class TestIssue108E {

	@EJB
	private ManagerE mgr;

	@Module
	public EnterpriseBean bean() {
		return new StatefulBean(ManagerE.class).localBean();
	}

	@Configuration
	public Properties config() {
		return new Properties() {
			{
				this.setProperty("ds", "new://Resource?type=DataSource");
				this.setProperty("ds.JdbcUrl", "jdbc:hsqldb:mem:test");
				this.setProperty("ds.JdbcDriver", "org.hsqldb.jdbcDriver");
				this.setProperty("ds.UserName", "sa");
				this.setProperty("ds.Password", "");
			}
		};
	}

	@Test
	public void go() {
		Assert.assertSame(this.mgr.persist(), this.mgr.find());
	}

	@Module
	public PersistenceUnit jpa() {
		final PersistenceUnit u = new PersistenceUnit();

		u.setProvider(BatooPersistenceProvider.class);
		u.setName("d");
		u.setProperty("org.batoo.jpa.ddl", "drop");
		u.setProperty("org.batoo.jpa.sql_logging", "STDOUT");
		u.getClazz().add(Batoo.class.getName());

		return u;
	}
}
