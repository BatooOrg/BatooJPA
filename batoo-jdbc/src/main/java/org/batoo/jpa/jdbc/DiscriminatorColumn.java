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
package org.batoo.jpa.jdbc;

import java.sql.Connection;
import java.sql.Types;

import javax.persistence.DiscriminatorType;

import org.batoo.jpa.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.jdbc.mapping.Mapping;
import org.batoo.jpa.parser.metadata.DiscriminatorColumnMetadata;

/**
 * Column implementation for discriminator columns.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class DiscriminatorColumn extends AbstractColumn {

	/**
	 * Returns the java type of the discriminator column.
	 * 
	 * @param metadata
	 * @return the java type of the discriminator column
	 * 
	 * @since 2.0.0
	 */
	private static Class<?> getJavaType(DiscriminatorColumnMetadata metadata) {
		if (metadata == null) {
			return String.class;
		}

		switch (metadata.getDiscriminatorType()) {
			case STRING:
				return String.class;
			case INTEGER:
				return Integer.class;
			default:
				return char.class;
		}
	}

	private final EntityTable table;
	private final String columnDefinition;
	private final DiscriminatorType discriminatorType;
	private final int length;

	private final String name;

	/**
	 * @param jdbcAdaptor
	 *            the JDBC adaptor
	 * @param primaryTable
	 *            the primary table
	 * @param metadata
	 *            the metadata
	 * 
	 * @since 2.0.0
	 */
	public DiscriminatorColumn(JdbcAdaptor jdbcAdaptor, EntityTable primaryTable, DiscriminatorColumnMetadata metadata) {
		super(DiscriminatorColumn.getJavaType(metadata), null, null, null, false, metadata != null ? metadata.getLocator() : null);

		this.name = jdbcAdaptor.escape(metadata != null ? metadata.getName() : "DTYPE");
		this.table = primaryTable;

		this.columnDefinition = metadata != null ? metadata.getColumnDefinition() : null;
		this.discriminatorType = metadata != null ? metadata.getDiscriminatorType() : DiscriminatorType.STRING;
		this.length = metadata != null ? (metadata.getDiscriminatorType() == DiscriminatorType.CHAR ? 1 : metadata.getLength()) : 31;

		this.table.addColumn(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getColumnDefinition() {
		return this.columnDefinition;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getLength() {
		return this.length;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Mapping<?, ?, ?> getMapping() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getPrecision() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getScale() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getSqlType() {
		switch (this.discriminatorType) {
			case CHAR:
			case STRING:
				return Types.VARCHAR;
			default:
				return Types.INTEGER;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractTable getTable() {
		return this.table;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getTableName() {
		return this.table.getName();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object getValue(Connection connection, Object instance) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isInsertable() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isNullable() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isUnique() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isUpdatable() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setTable(AbstractTable table) {
		// noop
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setValue(Object instance, Object value) {
		// noop
	}
}
