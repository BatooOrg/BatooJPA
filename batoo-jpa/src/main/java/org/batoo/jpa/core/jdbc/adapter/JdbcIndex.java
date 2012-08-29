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
package org.batoo.jpa.core.jdbc.adapter;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * The metadata for an index of an existing table.
 * 
 * @author hceylan
 * @since $version
 */
public class JdbcIndex {

	private final String name;
	private final List<String> columns = Lists.newArrayList();

	/**
	 * @param name
	 *            the name of the index
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JdbcIndex(String name) {
		super();

		this.name = name;
	}

	/**
	 * Adds a column to the index.
	 * 
	 * @param columnName
	 *            the name of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void addColumn(String columnName) {
		this.columns.add(columnName);
	}

	/**
	 * Returns the name of the index.
	 * 
	 * @return the name of the index
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getName() {
		return this.name;
	}

}
