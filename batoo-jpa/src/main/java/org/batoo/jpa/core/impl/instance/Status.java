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
package org.batoo.jpa.core.impl.instance;

/**
 * The states for a managed instance
 * 
 * @author hceylan
 * @since $version
 */
public enum Status {
	/**
	 * Instance is new
	 */
	NEW,

	/**
	 * Instance is managed
	 */
	MANAGED,

	/**
	 * Instance is removed
	 */
	REMOVED,

	/**
	 * Instance is detached
	 */
	DETACHED,
}