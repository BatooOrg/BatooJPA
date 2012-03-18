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
package org.batoo.jpa.core.jdbc.adapter;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.SequenceGenerator;
import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;
import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.jdbc.PhysicalColumn;
import org.batoo.jpa.core.impl.jdbc.SingleValueHandler;
import org.batoo.jpa.core.jdbc.Column;
import org.batoo.jpa.core.jdbc.IdType;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * JDBC Adapter for Derby.
 * 
 * @author hceylan
 * @since $version
 */
public class DerbyAdapter extends JDBCAdapter {

	private static final String[] DRIVER_CLASSES = new String[] { "org.apache.derby.jdbc.Driver40", "org.apache.derby.jdbc.EmbeddedDriver" };

	/**
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public DerbyAdapter() throws MappingException {
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String createColumnDDL(Column columnDefinition) {
		final boolean identity = (columnDefinition.getIdType() == IdType.IDENTITY);

		return columnDefinition.getPhysicalName() + " " // name part
			+ this.getColumnType(columnDefinition, columnDefinition.getSqlType()) // data type part
			+ (!columnDefinition.isNullable() ? " NOT NULL" : "") // not null part
			+ (columnDefinition.isUnique() ? " UNIQUE" : "") // not null part
			+ (identity ? " GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)" : ""); // auto increment part
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void createForeignKey(DataSource dataSource, ForeignKey foreignKey) throws SQLException {
		final String tableName = foreignKey.getTableName();
		final String referenceTableName = foreignKey.getReferenceTableName();

		final String keyColumns = Joiner.on(", ").join(Lists.transform(foreignKey.getColumns(), new Function<PhysicalColumn, String>() {

			@Override
			public String apply(PhysicalColumn input) {
				return input.getPhysicalName();
			}
		}));

		final String foreignKeyColumns = Joiner.on(", ").join(
			Lists.transform(foreignKey.getColumns(), new Function<PhysicalColumn, String>() {

				@Override
				public String apply(PhysicalColumn input) {
					return input.getReferencedColumn().getPhysicalName();
				}
			}));

		final String sql = "ALTER TABLE " + tableName //
			+ "\n\tADD FOREIGN KEY (" + keyColumns + ")" //
			+ "\n\tREFERENCES " + referenceTableName + "(" + foreignKeyColumns + ")";

		new QueryRunner(dataSource).update(sql);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void createSchemaIfNecessary(DataSource datasource, String schema) throws SQLException {
		if (!this.schemaExists(datasource, schema)) {
			new QueryRunner(datasource).update("CREATE SCHEMA " + schema);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void createSequenceIfNecessary(DataSource datasource, SequenceGenerator sequence) throws SQLException {
		final boolean exists = new QueryRunner(datasource) //
		.query("SELECT SEQUENCENAME FROM SYS.SYSSEQUENCES WHERE SEQUENCENAME = ?", //
			new SingleValueHandler<String>(), sequence.sequenceName()) != null;

		if (!exists) {
			final String sql = "CREATE SEQUENCE " //
				+ (StringUtils.isNotBlank(sequence.schema()) ? sequence.schema() + "." : "") //
				+ sequence.sequenceName() // ;
				+ " START WITH " + sequence.initialValue() //
				+ " INCREMENT BY " + sequence.allocationSize();

			new QueryRunner(datasource).update(sql);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void dropSchema(DataSource datasource, String schema) throws SQLException {
		if (this.schemaExists(datasource, schema)) {
			final QueryRunner runner = new QueryRunner(datasource);

			// Derby requires all the objects deleted from the schema before it can be dropped
			// Drop Constraints
			final List<Object[]> foreignKeys = runner.query("SELECT T.TABLENAME, C.CONSTRAINTNAME FROM SYS.SYSSCHEMAS S\n" + //
				"\tINNER JOIN SYS.SYSTABLES T ON T.SCHEMAID = S.SCHEMAID\n" + //
				"\tINNER JOIN SYS.SYSCONSTRAINTS C ON C.TABLEID = T.TABLEID\n" + //
				"WHERE S.SCHEMANAME = '" + schema + "' AND C.TYPE = 'F'", new ArrayListHandler());

			for (final Object[] foreignKey : foreignKeys) {
				runner.update("ALTER TABLE " + schema + "." + foreignKey[0] + " DROP FOREIGN KEY " + foreignKey[1]);
			}

			// Drop tables
			final List<Object[]> tables = runner.query("SELECT TABLENAME FROM SYS.SYSSCHEMAS S\n" + //
				"\tINNER JOIN SYS.SYSTABLES T ON S.SCHEMAID = T.SCHEMAID\n" + //
				"WHERE SCHEMANAME = '" + schema + "'", new ArrayListHandler());

			for (final Object[] table : tables) {
				runner.update("DROP TABLE " + schema + "." + table[0]);
			}

			// Drop sequences
			final List<Object[]> sequences = runner.query("SELECT SEQUENCENAME FROM SYS.SYSSCHEMAS S\n" + //
				"\tINNER JOIN SYS.SYSSEQUENCES Q ON S.SCHEMAID = Q.SCHEMAID\n" + //
				"WHERE SCHEMANAME = '" + schema + "'", new ArrayListHandler());

			for (final Object[] sequence : sequences) {
				runner.update("DROP SEQUENCE " + schema + "." + sequence[0] + " RESTRICT");
			}

			runner.update("DROP SCHEMA " + schema + " RESTRICT");
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected String getDatabaseName() {
		return "Derby";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getDefaultSchema(DataSource dataSource) {
		return "SA";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected String[] getJDBCDriverClassNames() {
		return DRIVER_CLASSES;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Integer getNextSequence(DataSourceImpl datasource, String sequenceName) throws SQLException {
		return new QueryRunner(datasource) //
		.query("VALUES (NEXT VALUE FOR " + sequenceName + ")", new SingleValueHandler<Integer>());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getSelectLastIdentitySql() {
		return "VALUES IDENTITY_VAL_LOCAL()";
	}

	private boolean schemaExists(DataSource datasource, String schema) throws SQLException {
		return new QueryRunner(datasource) //
		.query("SELECT SCHEMANAME FROM SYS.SYSSCHEMAS WHERE SCHEMANAME = ?", //
			new SingleValueHandler<String>(), schema) != null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public IdType supports(IdType idType) {
		if (idType == null) {
			return IdType.SEQUENCE;
		}

		return idType;
	}
}
