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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation to specify the behavioure for fetch joins.
 * 
 * @author asimarslan
 * @since $version
 */
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface FetchJoin {

	/**
	 * Returns the maximum allowed depth for the join.
	 * <p>
	 * -1 denotes that the association should never be joined.
	 * <p>
	 * 0 denotes that the default max org.batoo.jdbc.max_fetch_join_depth setting should be used.
	 * <p>
	 * Any positive value denotes that the association should be fetched using join provided depth is below maxdepth.
	 * 
	 * @return the maximum allowed depth for the join
	 * 
	 * @since $version
	 * @author hceylan
	 */
	int maxDepth() default -1;
}
