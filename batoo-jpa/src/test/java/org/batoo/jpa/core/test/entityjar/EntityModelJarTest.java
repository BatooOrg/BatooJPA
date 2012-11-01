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
package org.batoo.jpa.core.test.entityjar;

import java.sql.SQLException;
import java.util.Set;

import javax.persistence.metamodel.EntityType;
import javax.sql.DataSource;

import junit.framework.Assert;

import org.batoo.jpa.core.impl.jdbc.dbutils.QueryRunner;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.NullResultSetHandler;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class EntityModelJarTest extends BaseCoreTest {

	private Foo newFoo() {
		final Foo foo = new Foo();

		foo.setValue("test");

		return foo;
	}

	/**
	 * Tests Entity Lists
	 * 
	 * @throws SQLException
	 *             thrown if fails
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testEntitySet() throws SQLException {
		final MetamodelImpl metamodel = this.emf().getMetamodel();
		final Set<EntityType<?>> entities = metamodel.getEntities();

		Assert.assertEquals(1, entities.size());

		Assert.assertNotNull(metamodel.getEntity(Foo.class));
		Assert.assertNull(metamodel.getEntity(Person.class));
		Assert.assertNull(metamodel.getEntity(Address.class));

		final DataSource dataSource = this.em().unwrap(DataSource.class);
		new QueryRunner(dataSource).query("SELECT * FROM Foo", new NullResultSetHandler());

	}

}
