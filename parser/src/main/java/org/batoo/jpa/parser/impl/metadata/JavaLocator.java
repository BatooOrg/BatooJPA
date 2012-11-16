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
package org.batoo.jpa.parser.impl.metadata;

import java.lang.reflect.Member;

import org.batoo.common.reflect.ReflectHelper;
import org.batoo.jpa.parser.impl.AbstractLocator;

/**
 * Locator for the java locations.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class JavaLocator extends AbstractLocator {

	private final Member member;
	private final Class<?> clazz;

	/**
	 * Constructs a new {@link JavaLocator} with class location.
	 * 
	 * @param clazz
	 *            the class
	 * 
	 * @since 2.0.0
	 */
	public JavaLocator(Class<?> clazz) {
		super();

		this.clazz = clazz;

		this.member = null;
	}

	/**
	 * Constructs a new {@link JavaLocator} with member location.
	 * 
	 * @param member
	 *            the member
	 * 
	 * @since 2.0.0
	 */
	public JavaLocator(Member member) {
		super();

		this.member = member;

		this.clazz = null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		if (this.member != null) {
			return ReflectHelper.createMemberName(this.member);
		}

		return this.clazz.getName();
	}

}
