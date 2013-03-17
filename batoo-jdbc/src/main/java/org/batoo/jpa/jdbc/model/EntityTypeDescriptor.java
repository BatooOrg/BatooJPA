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
package org.batoo.jpa.jdbc.model;

import org.batoo.common.reflect.AbstractAccessor;
import org.batoo.jpa.core.util.Pair;
import org.batoo.jpa.jdbc.AbstractTable;
import org.batoo.jpa.jdbc.EntityTable;
import org.batoo.jpa.jdbc.mapping.SingularMapping;

/**
 * The descriptor interface for JPA and Batoo DB entities.
 * 
 * @author hceylan
 * @since 2.0.1
 */
public interface EntityTypeDescriptor extends ManagedTypeDescriptor {

	/**
	 * Returns the discriminator value of the entity.
	 * 
	 * @return the discriminator value of the entity
	 * 
	 * @since 2.0.1
	 */
	Object getDiscriminatorValue();

	/**
	 * Returns the id class of the identifiable type.
	 * 
	 * @return the id class of the identifiable type
	 * 
	 * @since 2.0.0
	 */
	Class<?> getIdClass();

	/**
	 * Returns the id mapping of the entity.
	 * 
	 * @return the id mapping of the entity
	 * 
	 * @since 2.0.1
	 */
	SingularMapping<?, ?> getIdMapping();

	/**
	 * Returns an array of id attributes.
	 * 
	 * @return an array of id attributes
	 * 
	 * @since 2.0.0
	 */
	Pair<SingularMapping<?, ?>, AbstractAccessor>[] getIdMappings();

	/**
	 * Returns the name of the entity.
	 * 
	 * @return the name of the entity
	 * 
	 * @since 2.0.1
	 */
	String getName();

	/**
	 * Returns the primary table of the entity.
	 * 
	 * @return the primary table of the entity
	 * 
	 * @since 2.0.1
	 */
	EntityTable getPrimaryTable();

	/**
	 * Returns the table with the name.
	 * <p>
	 * If the <code>tableName</code> is blank then the primary table is returned
	 * 
	 * @param tableName
	 *            the name of the table, may be blank
	 * @return the table or null if not found
	 * 
	 * @since 2.0.0
	 */
	AbstractTable getTable(String tableName);

	/**
	 * Returns if the entity has single id mapping.
	 * 
	 * @return <code>true</code> if the entity has single id mapping, <code>false</code> otherwise
	 * 
	 * @since 2.0.1
	 */
	boolean hasSingleIdAttribute();
}
