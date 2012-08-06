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

import java.util.Arrays;
import java.util.Collection;

import org.batoo.jpa.parser.impl.AbstractLocator;

import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;

/**
 * 
 * Thrown when ORM XML or annotations' parsing encountered an error.
 * 
 * @author hceylan
 * @since $version
 */
public class MappingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private static String getLocation(AbstractLocator[] locators) {
		if (locators == null) {
			return "";
		}

		final Collection<AbstractLocator> filteredLocators = Collections2.filter(Arrays.asList(locators),
			Predicates.not(Predicates.isNull()));
		if (filteredLocators.size() == 0) {
			return "";
		}

		return " Defined at:" + (filteredLocators.size() > 1 ? "\n\t" : " ") + Joiner.on("\n\t").skipNulls().join(filteredLocators);
	}

	/**
	 * @param message
	 *            the message to prepend to the constructed message
	 * @param locators
	 *            the locators
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MappingException(String message, AbstractLocator... locators) {
		super(message + MappingException.getLocation(locators));
	}

	/**
	 * @param message
	 *            the message to prepend to the constructed message
	 * @param cause
	 *            the cause of the exception
	 * @param locators
	 *            the locators
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MappingException(String message, Throwable cause, AbstractLocator... locators) {
		super(message + MappingException.getLocation(locators), cause);
	}

}
