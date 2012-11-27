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

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.SingletonBean;
import org.apache.openejb.jee.jpa.unit.PersistenceUnit;
import org.apache.openejb.junit.ApplicationComposer;
import org.apache.openejb.junit.Module;
import org.batoo.jpa.core.BatooPersistenceProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ApplicationComposer.class)
@SuppressWarnings("javadoc")
public class TestIssue108 {

	private ClassLoader oldContextClassLoader;

	@PersistenceContext
	EntityManager em;

	@EJB
	Manager mgr;

	@Before
	public void _setup() {
		final Thread currentThread = Thread.currentThread();

		if (this.oldContextClassLoader != null) {
			currentThread.setContextClassLoader(this.oldContextClassLoader);
		}

		this.oldContextClassLoader = currentThread.getContextClassLoader();
	}

	@After
	public void _tearDown() {
		Thread.currentThread().setContextClassLoader(this.oldContextClassLoader);
	}

	@Module
	public EnterpriseBean bean() {
		return new SingletonBean(Manager.class).localBean();
	}

	@Test
	public void go() {
		this.mgr.persist();
		this.em.createQuery("select e from Batoo e").getSingleResult();
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
