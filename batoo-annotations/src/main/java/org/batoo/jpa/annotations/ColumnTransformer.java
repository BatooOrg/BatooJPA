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
package org.batoo.jpa.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used for a basic attribute to map a custom sql expression instead of a simple
 * column name . The write expression must contain exactly one '?' placeholder
 * for the value. <br/>
 * <br/>
 * For example:
 * <code>@ColumnTransformer(read="sqlExpression" write="sqlExpressionWithCustomParam(?)")</code>
 * 
 * @since 2.0.0
 * @author asimarslan
 */
@Target({ FIELD, METHOD })
@Retention(RUNTIME)
public @interface ColumnTransformer {

	/**
	 * Custom SQL read expression to load data
	 */
	String read() default "";

	/**
	 * Custom SQL write expression to persist/update data.
	 */
	String write() default "";
}
