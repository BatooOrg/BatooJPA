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
package org.batoo.jpa.core.impl.jdbc;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.parser.metadata.PrimaryKeyJoinColumnMetadata;
import org.batoo.jpa.parser.metadata.SecondaryTableMetadata;
import org.batoo.jpa.parser.metadata.TableMetadata;

/**
 * Table representing an secondary table for entity persistent storage.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class SecondaryTable extends EntityTable {

	private List<PrimaryKeyJoinColumnMetadata> metadata;
	private ForeignKey foreignKey;

	/**
	 * Default secondary table constructor.
	 * 
	 * @param entity
	 *            the entity
	 * @param metadata
	 *            the table metadata
	 * 
	 * @since 2.0.0
	 */
	public SecondaryTable(EntityTypeImpl<?> entity, SecondaryTableMetadata metadata) {
		super(entity, metadata);

		this.metadata = metadata.getPrimaryKeyJoinColumnMetadata();
	}

	/**
	 * Constructor primary table as join table in inheritance.
	 * 
	 * @param entity
	 *            the root entity
	 * @param metadata
	 *            the metadata
	 * 
	 * @since 2.0.0
	 */
	public SecondaryTable(EntityTypeImpl<?> entity, TableMetadata metadata) {
		super(entity, metadata);
	}

	/**
	 * @param primaryTableAlias
	 *            the primary table alias
	 * @param alias
	 *            the table alias
	 * @return the join SQL fragment
	 * 
	 * @since 2.0.0
	 */
	public String joinPrimary(String primaryTableAlias, String alias) {
		return this.foreignKey.createSourceJoin(JoinType.LEFT, primaryTableAlias, alias);
	}

	/**
	 * Links the secondary table to the primary table
	 * 
	 * @since 2.0.0
	 */
	public void link() {
		if (this.foreignKey == null) {
			this.foreignKey = new ForeignKey(this.getJdbcAdaptor(), this, this.getEntity(), this.metadata);
		}
	}
}
