/*
 * Copyright (c) 2008, 2009 Sun Microsystems. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Linda DeMichiel - Java Persistence 2.0 - Version 2.0 (October 1, 2009)
 *     Specification available from http://jcp.org/en/jsr/detail?id=317
 */

// $Id: UniqueConstraint.java 20957 2011-06-13 09:58:51Z stliu $

package javax.persistence;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Specifies that a unique constraint is to be included in
 * the generated DDL for a primary or secondary table.
 *
 * <pre>
 *    Example:
 *    &#064;Entity
 *    &#064;Table(
 *        name="EMPLOYEE",
 *        uniqueConstraints=
 *            &#064;UniqueConstraint(columnNames={"EMP_ID", "EMP_NAME"})
 *    )
 *    public class Employee { ... }
 * </pre>
 *
 * @since Java Persistence 1.0
 */
@Target({ })
@Retention(RUNTIME)
public @interface UniqueConstraint {
	/**
	 * (Optional) Constraint name.  A provider-chosen name will be chosen
	 * if a name is not specified.
	 *
	 * @since Java Persistence 2.0
	 */
	String name() default "";

	/**
	 * (Required) An array of the column names that make up the constraint.
	 */
	String[] columnNames();
}
