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
package org.batoo.jpa.core.test;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.dbutils.ResultSetHandler;

import com.google.common.collect.Lists;

/**
 * A Result set handler that lists the columns in a String.
 * 
 * @author hceylan
 * @since $version
 */
public class ColumnNameListHandler implements ResultSetHandler<String> {

	/**
	 * 
	 * @since $version
	 */
	public ColumnNameListHandler() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String handle(ResultSet rs) throws SQLException {
		final List<String> list = Lists.newArrayList();

		final ResultSetMetaData metaData = rs.getMetaData();

		final int columnCount = metaData.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			list.add(metaData.getColumnName(i).toLowerCase(Locale.ENGLISH));
		}

		Collections.sort(list);

		return list.toString();
	}
}
