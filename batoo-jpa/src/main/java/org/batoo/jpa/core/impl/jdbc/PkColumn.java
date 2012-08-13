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

import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.jdbc.IdType;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.metadata.ColumnMetadata;

/**
 * BasicColumn to persist id attributes of the entity.
 * 
 * @author hceylan
 * @since $version
 */
public class PkColumn extends BasicColumn {

	private final IdType idType;

	/**
	 * @param jdbcAdaptor
	 *            the jdbc adaptor
	 * @param mapping
	 *            the mapping
	 * @param sqlType
	 *            the SQL type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PkColumn(JdbcAdaptor jdbcAdaptor, BasicMapping<?, ?> mapping, int sqlType, ColumnMetadata metadata) {
		super(jdbcAdaptor, mapping, sqlType, metadata);

		this.idType = mapping.getAttribute().getIdType();
	}

	/**
	 * Returns the idType of the column.
	 * 
	 * @return the idType of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public IdType getIdType() {
		return this.idType;
	}
}
