/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.batoo.jpa.core.impl.jdbc;

import javax.persistence.PersistenceException;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.instance.ManagedInstance.Status;
import org.batoo.jpa.core.impl.mapping.AbstractPhysicalMapping;
import org.batoo.jpa.core.impl.mapping.BasicColumnTemplate;
import org.batoo.jpa.core.impl.mapping.JoinColumnTemplate;
import org.batoo.jpa.core.jdbc.Column;
import org.batoo.jpa.core.jdbc.IdType;
import org.batoo.jpa.core.jdbc.Table;

/**
 * Implementation of {@link Column}
 * 
 * @author hceylan
 * @since $version
 */
public class PhysicalColumn implements Column {

	private final AbstractPhysicalMapping<?, ?> mapping;
	private final PhysicalColumn referencedColumn;

	private final Table table;

	private final String name;
	private final String physicalName;
	private final IdType idType;

	private final int sqlType;

	private final int length;
	private final int precision;
	private final int scale;
	private final boolean nullable;
	private final boolean unique;

	private int h;

	/**
	 * @param mapping
	 *            the mapping
	 * @param referencedColumn
	 *            the referenced physical column, may be null
	 * @param table
	 *            the physical table the column belongs to
	 * @param basic
	 *            the template to use
	 * @param sqlType
	 *            the SQL type of the column
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PhysicalColumn(AbstractPhysicalMapping<?, ?> mapping, AbstractTable table, BasicColumnTemplate<?, ?> basic, int sqlType)
		throws MappingException {
		this(mapping, null, table, basic.getName(), basic.getIdType(), sqlType, basic.getLength(), basic.getPrecision(), basic.getScale(),
			basic.isNullable(), basic.isUnique());

		this.mapping.addColumn(this);
		basic.setPhysicalColumn(this);
	}

	/**
	 * @param mapping
	 *            the mapping
	 * @param table
	 *            the physical table the column belongs to
	 * @param join
	 *            the name of the column
	 * @param referencedColumn
	 *            the referenced physical column, may be null
	 * @param sqlType
	 *            the SQL type of the column
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PhysicalColumn(AbstractPhysicalMapping<?, ?> mapping, AbstractTable table, JoinColumnTemplate<?, ?> join,
		PhysicalColumn referencedColumn, int sqlType) throws MappingException {
		this(mapping, referencedColumn, table, join.getName(), null, sqlType, referencedColumn.getLength(),
			referencedColumn.getPrecision(), referencedColumn.getScale(), join.isNullable(), join.isUnique());

		this.mapping.addColumn(this);
	}

	/**
	 * @param mapping
	 *            the mapping
	 * @param referencedColumn
	 *            the referenced physical column, may be null
	 * @param table
	 *            the physical table the column belongs to
	 * @param name
	 *            the name of the column
	 * @param idType
	 *            the idType of the column
	 * @param sqlType
	 *            the SQL type of the column
	 * @param length
	 *            the length of the column
	 * @param precision
	 *            the precision of the column
	 * @param scale
	 *            the scale of the column
	 * @param nullable
	 *            if column is nullable
	 * @param unique
	 *            if column is unique
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private PhysicalColumn(AbstractPhysicalMapping<?, ?> mapping, PhysicalColumn referencedColumn, AbstractTable table, String name,
		IdType idType, int sqlType, int length, int precision, int scale, boolean nullable, boolean unique) throws MappingException {
		super();

		this.mapping = mapping;
		this.referencedColumn = referencedColumn;

		this.table = table;
		this.name = StringUtils.isNotBlank(name) ? name : mapping.getDeclaringAttribute().getName();
		this.physicalName = table.getJdbcAdapter().escape(this.name);
		this.idType = idType;
		this.sqlType = sqlType;
		this.length = length;
		this.precision = precision;
		this.scale = scale;
		this.nullable = nullable;
		this.unique = unique;

		table.addColumn(this);
	}

	/**
	 * Constructor used for secondary table primary key columns
	 * 
	 * @param table
	 *            the name
	 * @param column
	 *            the original primary key column
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PhysicalColumn(AbstractTable table, PhysicalColumn column) throws MappingException {
		this(column.getMapping(), column, table, column.getName(), IdType.MANUAL, column.getSqlType(), column.getLength(),
			column.getPrecision(), column.getScale(), false, false);
	}

	/**
	 * Constructor used for secondary table primary key columns
	 * 
	 * @param table
	 *            the name
	 * @param prefix
	 *            the prefix for the column name
	 * @param column
	 *            the original primary key column
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PhysicalColumn(JoinTable table, String prefix, PhysicalColumn column) throws MappingException {
		this(column.getMapping(), column, table, prefix + "_" + column.getName(), IdType.MANUAL, column.getSqlType(), column.getLength(),
			column.getPrecision(), column.getScale(), false, false);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final PhysicalColumn other = (PhysicalColumn) obj;
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		}
		else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.table == null) {
			if (other.table != null) {
				return false;
			}
		}
		else if (!this.table.equals(other.table)) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final IdType getIdType() {
		return this.idType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final int getLength() {
		return this.length;
	}

	/**
	 * Returns the mapping.
	 * 
	 * @return the mapping
	 * @since $version
	 */
	public AbstractPhysicalMapping<?, ?> getMapping() {
		return this.mapping;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getPhysicalName() {
		return this.physicalName;
	}

	/**
	 * Returns the physical value for the column.
	 * 
	 * @param instance
	 *            the instance
	 * @return the physical value for the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Object getPhysicalValue(SessionImpl session, Object instance) {
		Object value = this.mapping.getValue(instance);

		if ((!this.isId() && !this.getTable().isPrimary()) || this.getTable().isPrimary()) {
			if ((value != null) && (this.referencedColumn != null)) {

				final ManagedInstance<? super Object> reference = session.get(value);
				if ((reference == null) || (reference.getStatus() != Status.MANAGED)) {
					throw new PersistenceException(instance + " has a reference with " + this.mapping.getPathAsString() + " to " + value
						+ " that is not managed (" + (reference != null ? reference.getStatus() : Status.NEW) + ")");
				}

				value = this.referencedColumn.getMapping().getValue(value);
			}
		}

		return value;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final int getPrecision() {
		return this.precision;
	}

	/**
	 * Returns the referenced physical column.
	 * 
	 * @return the referenced physical column
	 * @since $version
	 */
	public PhysicalColumn getReferencedColumn() {
		return this.referencedColumn;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final int getScale() {
		return this.scale;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final int getSqlType() {
		return this.sqlType;
	}

	/**
	 * Returns the table.
	 * 
	 * @return the table
	 * @since $version
	 */
	public final Table getTable() {
		return this.table;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		if (this.h != 0) {
			return this.h;
		}

		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.name == null) ? 0 : this.name.hashCode());
		this.h = (prime * result) + ((this.table == null) ? 0 : this.table.hashCode());

		return this.h;
	}

	/**
	 * Returns if the column is id column.
	 * 
	 * @return true if the column is id column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isId() {
		return this.idType != null;
	}

	/**
	 * Returns the nullable.
	 * 
	 * @return the nullable
	 * @since $version
	 */
	@Override
	public boolean isNullable() {
		return this.nullable;
	}

	/**
	 * Returns the unique.
	 * 
	 * @return the unique
	 * @since $version
	 */
	@Override
	public final boolean isUnique() {
		return this.unique;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "PhysicalColumn [table=" + this.table.getName() + ", name=" + this.name + ", idType=" + this.idType + ", sqlType="
			+ this.sqlType + ", length=" + this.length + ", precision=" + this.precision + ", scale=" + this.scale + ", nullable="
			+ this.nullable + ", unique=" + this.unique + "]";
	}

}
