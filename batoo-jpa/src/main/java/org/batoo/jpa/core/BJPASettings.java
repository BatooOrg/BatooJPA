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
package org.batoo.jpa.core;

/**
 * @author hceylan
 * 
 * @since $version
 */
public interface BJPASettings {

	/**
	 * The warn time for SQL operations taking too long.
	 */
	final long WARN_TIME = 2500;

	/**
	 * Class loader to use during deployment, default null
	 */
	String CLASS_LOADER_CLASS = "org.batoo.jpa.class_loader_class";

	/**
	 * The root package for the persistence unit to scan for entities, default null
	 */
	String ROOT_PACKAGE = "org.batoo.jpa.root_url";

	/**
	 * DDL operations, DROP | CREATE (*) | UPDATE | VERIFY | NONE
	 */
	String DDL = "org.batoo.jpa.ddl";

}
