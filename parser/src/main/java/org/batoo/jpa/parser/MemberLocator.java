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
package org.batoo.jpa.parser;

import java.lang.reflect.Member;

/**
 * A simple locator that has it's <code>toString()</code> be that of the supplied {@link Member}. Useful when a {@link MappingException} is
 * thrown to show the location of the error.
 * 
 * @author Bobby Walters
 * @since 2.0.1
 */
public final class MemberLocator extends AbstractLocator {
	private final Member member;

	public MemberLocator(final Member m) {
		member = m;
	}

	public String toString() {
		return member.toString();
	}
}
