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
package org.batoo.jpa.core.impl.cache;

import java.util.Arrays;

/**
 * Cache reference for query result lists.
 * 
 * @author hceylan
 * @since $version
 */
public class ResultListReference {

	private final String sql;
	private final Object[] parameters;

	/**
	 * @param sql
	 *            the sql
	 * @param parameters
	 *            the parameters
	 * 
	 * @since $version
	 */
	public ResultListReference(String sql, Object[] parameters) {
		super();

		this.sql = sql;
		this.parameters = parameters != null ? Arrays.copyOf(parameters, parameters.length) : null;
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

		final ResultListReference other = (ResultListReference) obj;
		if (!Arrays.equals(this.parameters, other.parameters)) {
			return false;
		}

		if (!this.sql.equals(other.sql)) {
			return false;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;

		int result = 1;
		result = (prime * result) + Arrays.hashCode(this.parameters);
		result = (prime * result) + ((this.sql == null) ? 0 : this.sql.hashCode());

		return result;
	}
}
