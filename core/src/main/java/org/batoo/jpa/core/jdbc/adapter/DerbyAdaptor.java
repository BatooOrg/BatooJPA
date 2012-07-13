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

import javax.persistence.GenerationType;
import javax.persistence.LockModeType;
import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.batoo.jpa.core.impl.jdbc.AbstractColumn;
import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;
import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.jdbc.JoinColumn;
import org.batoo.jpa.core.impl.jdbc.PkColumn;
import org.batoo.jpa.core.impl.jdbc.SingleValueHandler;
import org.batoo.jpa.core.impl.model.SequenceGenerator;
import org.batoo.jpa.core.impl.model.TableGenerator;
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
public class DerbyAdaptor extends JdbcAdaptor {

	private static final String[] DRIVER_CLASSES = new String[] { "org.apache.derby.jdbc.Driver40", "org.apache.derby.jdbc.EmbeddedDriver" };

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public DerbyAdaptor() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String applyLock(String sql, LockModeType lockMode) {
		switch (lockMode) {
			case PESSIMISTIC_FORCE_INCREMENT:
			case PESSIMISTIC_READ:
				return sql + "\nFOR READ ONLY";
			case PESSIMISTIC_WRITE:
				return sql + "\nFOR UPDATE";
		}

		return sql;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String createColumnDDL(AbstractColumn column) {
		final boolean identity = (column instanceof PkColumn) && (((PkColumn) column).getIdType() == IdType.IDENTITY);

		return column.getName() + " " // name part
			+ this.getColumnType(column, column.getSqlType()) // data type part
			+ (!column.isNullable() ? " NOT NULL" : "") // not null part
			+ (column.isUnique() ? " UNIQUE" : "") // not null part
			+ (identity ? " GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)" : ""); // auto increment part
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public synchronized void createForeignKey(DataSource datasource, ForeignKey foreignKey) throws SQLException {
		final String referenceTableName = foreignKey.getReferencedTableName();
		final String tableName = foreignKey.getTable().getName();

		final String foreignKeyColumns = Joiner.on(", ").join(Lists.transform(foreignKey.getJoinColumns(), new Function<JoinColumn, String>() {

			@Override
			public String apply(JoinColumn input) {
				return input.getReferencedColumnName();
			}
		}));

		final String keyColumns = Joiner.on(", ").join(Lists.transform(foreignKey.getJoinColumns(), new Function<JoinColumn, String>() {

			@Override
			public String apply(JoinColumn input) {
				return input.getName();
			}
		}));

		final String sql = "ALTER TABLE " + tableName //
			+ "\n\tADD FOREIGN KEY (" + keyColumns + ")" //
			+ "\n\tREFERENCES " + referenceTableName + "(" + foreignKeyColumns + ")";

		new QueryRunner(datasource).update(sql);
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
		final String schema = this.schemaOf(datasource, sequence.getSchema());

		final boolean exists = new QueryRunner(datasource) //
		.query("SELECT SEQUENCENAME FROM SYS.SYSSCHEMAS S\n" + //
			"\tINNER JOIN SYS.SYSSEQUENCES Q ON S.SCHEMAID = Q.SCHEMAID\n" + //
			"WHERE SCHEMANAME = ? AND SEQUENCENAME = ?", //
			new SingleValueHandler<String>(), schema, sequence.getSequenceName()) != null;

		if (!exists) {
			final String sql = "CREATE SEQUENCE " //
				+ schema + "." + sequence.getSequenceName() // ;
				+ " START WITH " + sequence.getInitialValue() //
				+ " INCREMENT BY " + sequence.getAllocationSize();

			new QueryRunner(datasource).update(sql);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void createTableGeneratorIfNecessary(DataSource datasource, TableGenerator table) throws SQLException {
		final String schema = this.schemaOf(datasource, table.getSchema());

		if (new QueryRunner(datasource).query("SELECT TABLENAME FROM SYS.SYSSCHEMAS S\n" + //
			"\tINNER JOIN SYS.SYSTABLES T ON S.SCHEMAID = T.SCHEMAID\n" + //
			"WHERE SCHEMANAME = ?", new SingleValueHandler<String>(), schema) == null) {

			final String sql = "CREATE TABLE " + schema + "." + table.getTable() + " ("//
				+ "\n\t" + table.getPkColumnName() + " VARCHAR(255)," //
				+ "\n\t" + table.getValueColumnName() + " INT," //
				+ "\nPRIMARY KEY(" + table.getPkColumnName() + "))";

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
				"WHERE S.SCHEMANAME = ? AND C.TYPE = 'F'", new ArrayListHandler(), schema);

			for (final Object[] foreignKey : foreignKeys) {
				runner.update("ALTER TABLE " + schema + "." + foreignKey[0] + " DROP FOREIGN KEY " + foreignKey[1]);
			}

			// Drop tables
			final List<Object[]> tables = runner.query("SELECT TABLENAME FROM SYS.SYSSCHEMAS S\n" + //
				"\tINNER JOIN SYS.SYSTABLES T ON S.SCHEMAID = T.SCHEMAID\n" + //
				"WHERE SCHEMANAME = ?", new ArrayListHandler(), schema);

			for (final Object[] table : tables) {
				runner.update("DROP TABLE " + schema + "." + table[0]);
			}

			// Drop sequences
			final List<Object[]> sequences = runner.query("SELECT SEQUENCENAME FROM SYS.SYSSCHEMAS S\n" + //
				"\tINNER JOIN SYS.SYSSEQUENCES Q ON S.SCHEMAID = Q.SCHEMAID\n" + //
				"WHERE SCHEMANAME = ?", new ArrayListHandler(), schema);

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
	protected String[] getJdbcDriverClassNames() {
		return DerbyAdaptor.DRIVER_CLASSES;
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
	public IdType supports(GenerationType type) {
		if (type == null) {
			return IdType.SEQUENCE;
		}

		switch (type) {
			case IDENTITY:
				return IdType.IDENTITY;
			case SEQUENCE:
				return IdType.SEQUENCE;
			case TABLE:
				return IdType.TABLE;
			default:
				return IdType.SEQUENCE;
		}
	}
}
