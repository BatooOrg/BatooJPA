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
package org.batoo.jpa.core.impl.criteria.expression;

/**
 * The types for the numberic functions.
 * 
 * @author hceylan
 * @since $version
 */
public enum DateTimeFunctionType {
	/**
	 * The 'second' function.
	 */
	SECOND("second({0}, {1})"),

	/**
	 * The 'minute' function.
	 */
	MINUTE("minute({0})"),

	/**
	 * The 'hour' function.
	 */
	HOUR("hour({0})"),

	/**
	 * The dayofmonth function.
	 */
	DAYOFMONTH("dayofmonth({0})"),

	/**
	 * The dayofweek function.
	 */
	DAYOFWEEK("dayofweek({0})"),

	/**
	 * The dayofyear function.
	 */
	DAYOFYEAR("dayofyear({0})"),

	/**
	 * The week function.
	 */
	WEEK("week({0})"),

	/**
	 * The month function.
	 */
	MONTH("month({0})"),

	/**
	 * The year function.
	 */
	YEAR("year({0})");

	final String jpqlFragment;

	private DateTimeFunctionType(String jpqlFragment) {
		this.jpqlFragment = jpqlFragment;
	}
}
