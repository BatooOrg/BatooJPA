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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.BLogger;

import com.google.common.collect.Lists;

/**
 * Logger to log query results in a table.
 * 
 * @author hceylan
 * @since $version
 */
public class ResultsetLogger {
	private static final BLogger LOG = BLogger.getLogger(ResultsetLogger.class);

	private static final int MAX_COL_LENGTH = 30;

	public static boolean isLogging() {
		return LOG.isDebugEnabled();
	}

	private String[] labels;

	private ArrayList<Object[]> data;

	private final ResultSetMetaData md;

	/**
	 * @param rs
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ResultsetLogger(ResultSet rs) throws SQLException {
		super();

		this.md = rs.getMetaData();

		// prepare the labels
		this.prepareLabels();
	}

	/**
	 * Dumps the resultset.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void dumpResultSet() {
		final int[] lengths = new int[this.labels.length];
		for (int i = 0; i < lengths.length; i++) {
			lengths[i] = this.max(lengths[i], StringUtils.length(this.labels[i]));
		}

		for (final Object[] data : this.data) {
			for (int i = 0; i < data.length; i++) {
				if (data[i] != null) {
					lengths[i] = this.max(lengths[i], StringUtils.length(data[i].toString()));
				}
			}
		}

		int length = 3;
		for (final int length2 : lengths) {
			length += length2 + 2;
		}

		final StringBuffer dump = new StringBuffer("\n");

		// the labels
		dump.append(StringUtils.repeat("-", length));
		dump.append("\n| ");

		for (int i = 0; i < this.labels.length; i++) {
			String strValue = StringUtils.abbreviate(this.labels[i], lengths[i]);
			strValue = StringUtils.rightPad(strValue, lengths[i]);

			dump.append(strValue);
			dump.append(" | ");
		}

		// the data
		dump.append("\n");
		dump.append(StringUtils.repeat("-", length));

		for (final Object[] data : this.data) {
			dump.append("\n| ");

			for (int i = 0; i < data.length; i++) {
				String strValue = data[i] != null ? data[i].toString() : "!NULL!";
				strValue = StringUtils.abbreviate(strValue, lengths[i]);
				if (data[i] instanceof Number) {
					strValue = StringUtils.leftPad(strValue, lengths[i]);
				}
				else {
					strValue = StringUtils.rightPad(strValue, lengths[i]);
				}

				dump.append(strValue);
				dump.append(" | ");
			}

		}

		dump.append("\n");
		dump.append(StringUtils.repeat("-", length));

		LOG.debug(dump.toString());
	}

	private int max(int length1, int length2) {
		return Math.min(MAX_COL_LENGTH, Math.max(length1, length2));
	}

	private void prepareLabels() throws SQLException {
		this.data = Lists.newArrayList();

		this.labels = new String[this.md.getColumnCount()];
		for (int i = 0; i < this.labels.length; i++) {
			String label = this.md.getColumnName(i + 1) + " (" + this.md.getColumnTypeName(i + 1) + ")";
			label = StringUtils.abbreviate(label, MAX_COL_LENGTH);

			this.labels[i] = label;
		}
	}

	/**
	 * Stores the data for the row.
	 * 
	 * @param rs
	 *            the resultset
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void storeData(ResultSet rs) throws SQLException {
		final Object[] data = new Object[this.md.getColumnCount()];

		for (int i = 0; i < this.labels.length; i++) {
			final Object value = rs.getObject(i + 1);
			if (value == null) {
				data[i] = "(null)";
			}
			else {
				data[i] = StringUtils.abbreviate(value.toString(), MAX_COL_LENGTH);
			}
		}

		this.data.add(data);
	}
}
